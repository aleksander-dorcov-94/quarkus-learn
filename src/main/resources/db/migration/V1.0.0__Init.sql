CREATE SEQUENCE address_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE company_seq INCREMENT BY 1 START WITH 1;
CREATE SEQUENCE employee_seq INCREMENT BY 1 START WITH 1;

CREATE TABLE company
(
    id   BIGINT NOT NULL PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE address
(
    id         BIGINT NOT NULL PRIMARY KEY,
    city       VARCHAR(255),
    street     VARCHAR(255),
    company_id BIGINT UNIQUE REFERENCES company (id) -- New FK location
);

CREATE TABLE employee
(
    id         BIGINT DEFAULT nextval('employee_seq') PRIMARY KEY,
    name       VARCHAR(255),
    company_id BIGINT,
    CONSTRAINT fk_employee_company FOREIGN KEY (company_id) REFERENCES company (id)
);
