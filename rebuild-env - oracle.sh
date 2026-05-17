#!/bin/bash
set -e

CYAN='\e[1;36m'
NC='\e[0m'

echo -e "${CYAN}------------------------------------------------------------${NC}"
echo -e "${CYAN}🚀 ORACLE LINUX 10: PURE GNOME + FLATPAK DEV SETUP${NC}"
echo -e "${CYAN}------------------------------------------------------------${NC}"

# Ask for sudo upfront
sudo -v
echo -e "${CYAN}🔧 Setting gnome shortcuts ${NC}"
gsettings set org.gnome.desktop.interface color-scheme 'prefer-dark'
gsettings set org.gnome.desktop.wm.keybindings unmaximize "['<Alt>F5']"
gsettings set org.gnome.desktop.wm.keybindings close "['<Super>q', '<Alt>F4']"
gsettings set org.gnome.desktop.wm.keybindings minimize "['<Super>Down']"
gsettings set org.gnome.desktop.input-sources sources "[('xkb', 'us'), ('xkb', 'bg+phonetic')]"
# display minimize/maximize button next to the close button
gsettings set org.gnome.desktop.wm.preferences button-layout "appmenu:minimize,maximize,close"
# add 125% scale option, needed for 4k monitors, otherwise it jumps for 100 to 200 with nothing in between
gsettings set org.gnome.mutter experimental-features "['scale-monitor-framebuffer']"

# --- 1. System Updates & Core Repositories ---
echo -e "${CYAN}🔧 Enabling EPEL 10, CRB 10, and updating system...${NC}"
sudo dnf install -y oracle-epel-release-el10
sudo dnf config-manager --set-enabled ol10_codeready_builder
sudo dnf update -y

# --- 2. GRUB & RTC Sync ---
echo -e "${CYAN}⏳ Optimizing GRUB and System Clock...${NC}"
sudo sed -i 's/^GRUB_TIMEOUT=.*/GRUB_TIMEOUT=1/' /etc/default/grub
sudo grub2-mkconfig -o /boot/grub2/grub.cfg
timedatectl set-local-rtc 1 --adjust-system-clock

# --- 3. Base CLI Dependencies (Native) ---
echo -e "${CYAN}📦 Installing base native utilities...${NC}"
sudo dnf install -y curl wget zip unzip git fontconfig tar gcc make kernel-headers dnf-plugins-core flatpak

# --- 4. External Repositories (For Native Docker/K8s) ---
echo -e "${CYAN}🔑 Adding Repositories for Docker and Kubernetes...${NC}"
sudo dnf config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo

cat <<EOF | sudo tee /etc/yum.repos.d/kubernetes.repo
[kubernetes]
name=Kubernetes
baseurl=https://pkgs.k8s.io/core:/stable:/v1.31/rpm/
enabled=1
gpgcheck=1
gpgkey=https://pkgs.k8s.io/core:/stable:/v1.31/rpm/repodata/repomd.xml.key
EOF

# --- 5. Native CLI Tools & Docker ---
echo -e "${CYAN}💻 Installing Native CLI Tools (Docker, Kubectl, Fastfetch, Btop)...${NC}"
sudo dnf install -y fastfetch kubectl docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin btop

echo -e "${CYAN}✨ Installing eza (Native Binary)...${NC}"
wget -qO eza.tar.gz https://github.com/eza-community/eza/releases/latest/download/eza_x86_64-unknown-linux-gnu.tar.gz
tar -xzf eza.tar.gz
sudo mv eza /usr/local/bin/
rm eza.tar.gz

# --- 6. Zsh & Oh My Zsh ---
echo -e "${CYAN}🐚 Setting up Zsh...${NC}"
sudo dnf install -y zsh util-linux-user
if [ ! -d "$HOME/.oh-my-zsh" ]; then
    sh -c "$(curl -fsSL https://raw.githubusercontent.com/ohmyzsh/ohmyzsh/master/tools/install.sh)" "" --unattended
fi
sudo chsh -s $(which zsh) $USER

# --- 7. Fonts ---
echo -e "${CYAN}🔡 Deploying JetBrains Mono Nerd Font...${NC}"
FONT_DIR="$HOME/.local/share/fonts"
mkdir -p "$FONT_DIR"
wget -qO JetBrainsMono.zip https://github.com/ryanoasis/nerd-fonts/releases/latest/download/JetBrainsMono.zip
unzip -q -o JetBrainsMono.zip -d "$FONT_DIR"
rm JetBrainsMono.zip
fc-cache -fv

# --- 8. More Native CLI Tools (Warp, Micro, Kubefwd, K9s) ---
echo -e "${CYAN}🌐 Downloading Warp Terminal, Micro, Kubefwd, and K9s...${NC}"
# Warp is installed natively via RPM because terminal emulators require deep system access
wget -qO warp-terminal.rpm "https://app.warp.dev/download?package=rpm"
sudo dnf localinstall -y ./warp-terminal.rpm
rm ./warp-terminal.rpm

# Micro
curl -s https://getmic.ro | bash
sudo install micro /usr/local/bin/ && rm -f micro

# K9s
curl -sS https://webi.sh/k9s | bash
sudo cp ~/.local/bin/k9s /usr/local/bin/

# Kubefwd
KFWD_VERSION=$(curl -s https://api.github.com/repos/txn2/kubefwd/releases/latest | grep tag_name | cut -d '"' -f 4)
wget -qO kubefwd_amd64.rpm "https://github.com/txn2/kubefwd/releases/download/${KFWD_VERSION}/kubefwd_amd64.rpm"
sudo dnf localinstall -y kubefwd_amd64.rpm
rm kubefwd_amd64.rpm

# --- 9. Flatpak GUI Applications ---
echo -e "${CYAN}📦 Setting up Flathub and installing GUI Apps...${NC}"
sudo flatpak remote-add --if-not-exists flathub https://dl.flathub.org/repo/flathub.flatpakrepo

#Flatsteal
flatpak install flathub com.github.tchx84.Flatseal

# Browsers
sudo flatpak install -y flathub com.google.Chrome
sudo flatpak install -y flathub com.brave.Browser

# Developer IDEs & Tools
sudo flatpak install -y flathub com.visualstudio.code
sudo flatpak install -y flathub com.getpostman.Postman

# IntelliJ IDEA (Community Edition by default. Swap "Community" for "Ultimate" if you have a paid license)
flatpak install flathub com.jetbrains.IntelliJ-IDEA-Ultimate

# Media
sudo flatpak install -y flathub org.videolan.VLC

# --- 10. Activating Docker ---
echo -e "${CYAN}🐳 Activating system Docker Engine...${NC}"
sudo systemctl unmask docker.service docker.socket
sudo systemctl enable --now docker.service docker.socket
sudo usermod -aG docker $USER

# --- 11. Zsh Aliases ---
echo -e "${CYAN}✅ Setting up ZSH Aliases ${NC}"
echo -e "${CYAN}📝 Writing Aliases...${NC}"
if ! grep -q "alias ls=" "$HOME/.zshrc"; then
    cat << 'EOF' >> "$HOME/.zshrc"

# --- Custom Dev Aliases ---
alias ls="eza --icons"
alias lst="eza --tree --icons --ignore-glob='node_modules|target|dist|build|.git|.idea|.vscode|.gradle|.mvn|coverage|.next|.nuxt|.angular|bower_components|__pycache__|.svn|.hg|.DS_Store|*.class|*.jar|*.war|*.ear|logs'"
alias nano="micro"
alias idea="flatpak run com.jetbrains.IntelliJ-IDEA-Ultimate &> /dev/null &"
alias code="flatpak run com.visualstudio.code &> /dev/null &"
EOF
fi

# --- 12. SDKMAN! & Java Setup ---
echo -e "${CYAN}☕ Configuring SDKMAN!...${NC}"
curl -s "https://get.sdkman.io" | bash

cat << 'EOF' > ./sdkman_install.sh
#!/bin/bash
export SDKMAN_DIR="$HOME/.sdkman"
[[ -s "$HOME/.sdkman/bin/sdkman-init.sh" ]] && source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 25.0.2-amzn
sdk install java 21.0.9-amzn
sdk install java 17.0.14-amzn
sdk install gradle 9.3.1 
sdk install maven 3.9.12
sdk default java 25.0.2-amzn
sdk default gradle 9.3.1
sdk default maven 3.9.12
source "$HOME/.sdkman/bin/sdkman-init.sh"
EOF
chmod +x ./sdkman_install.sh

echo -e "${CYAN}✅ SETUP COMPLETE! Rebooting in 5 seconds...${NC}"
sleep 5
sudo reboot
