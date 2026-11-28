CREATE TABLE IF NOT EXISTS category (
    category_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    daily_fee NUMERIC(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS car (
    car_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    license_plate VARCHAR(40) UNIQUE NOT NULL,
    category_id BIGINT NOT NULL REFERENCES category(category_id) ON DELETE RESTRICT,
    car_model VARCHAR(100) NOT NULL,
    car_manufacturer VARCHAR(100) NOT NULL,
    mileage INTEGER DEFAULT 0,
    status VARCHAR(40) NOT NULL CHECK (status IN ('AVAILABLE', 'RENTED', 'MAINTENANCE', 'RESERVED'))
);

CREATE TABLE IF NOT EXISTS customer (
    customer_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    e_mail VARCHAR(100) NOT NULL UNIQUE,
    is_regular_customer BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS rental (
    rental_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    car_id BIGINT NOT NULL REFERENCES car(car_id) ON DELETE CASCADE,
    customer_id BIGINT NOT NULL REFERENCES customer(customer_id) ON DELETE CASCADE,
    from_date DATE NOT NULL DEFAULT CURRENT_DATE,
    to_date DATE NOT NULL,
    return_date DATE,
    rental_fee NUMERIC(10, 2)
);

CREATE TABLE IF NOT EXISTS reserve (
    reserve_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customer(customer_id) ON DELETE CASCADE,
    car_id BIGINT NOT NULL REFERENCES car(car_id) ON DELETE CASCADE,
    from_date DATE NOT NULL DEFAULT CURRENT_DATE,
    to_date DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS service_log (
    service_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    car_id BIGINT NOT NULL REFERENCES car(car_id) ON DELETE CASCADE,
    from_date DATE NOT NULL DEFAULT CURRENT_DATE,
    to_date DATE NOT NULL,
    service_description TEXT,
    service_fee NUMERIC(10, 2) NOT NULL
);

INSERT INTO category (category_name, daily_fee) VALUES
('Sedan', 5000.00),
('Kombi', 10000.00),
('SUV', 8000.00),
('Van', 15000.00),
('Coupe', 12000.00),
('Ferdehátú', 6500.00),
('Kabrió', 18000.00);

INSERT INTO customer (first_name, last_name, e_mail, is_regular_customer) VALUES
('Anna', 'Nagy', 'nagyanna@gmail.com', TRUE),
('Péter', 'Kiss', 'kisspeter@gmail.com', FALSE),
('Éva', 'Kovács', 'kovacseva@gmail.com', FALSE),
('Tamás', 'Horváth', 'horvathtamas@gmail.com', TRUE),
('Gábor', 'Tóth', 'tothgabor@gmail.com', FALSE),
('Bence', 'Szabó', 'szabo.bence@gmail.com', TRUE),
('Katalin', 'Varga', 'varga.katalin@gmail.com', FALSE),
('Zoltán', 'Molnár', 'molnar.zoltan@gmail.com', TRUE),
('Dóra', 'Farkas', 'farkas.dora@gmail.com', FALSE),
('László', 'Balogh', 'balogh.laszlo@gmail.com', FALSE),
('Eszter', 'Papp', 'papp.eszter@gmail.com', TRUE),
('Balázs', 'Takács', 'takacs.balazs@gmail.com', FALSE);

INSERT INTO car (license_plate, category_id, car_manufacturer, car_model, mileage, status) VALUES
('AUD-001', 1, 'Audi', 'A6', 120000, 'AVAILABLE'),
('TOY-002', 3, 'Toyota', 'RAV4', 45000, 'AVAILABLE'),
('FOR-003', 4, 'Ford', 'Transit', 210000, 'AVAILABLE'),
('OPE-004', 6, 'Opel', 'Astra', 89000, 'MAINTENANCE'),
('MUS-005', 5, 'Ford', 'Mustang', 15000, 'AVAILABLE'),
('SKO-006', 2, 'Skoda', 'Octavia', 65000, 'AVAILABLE'),
('BMW-007', 1, 'BMW', '530e', 32000, 'RENTED'),
('AUD-101', 1, 'Audi', 'A4', 95000, 'AVAILABLE'),
('BMW-102', 2, 'BMW', '320d Touring', 115000, 'AVAILABLE'),
('VOL-103', 6, 'Volkswagen', 'Golf', 72000, 'AVAILABLE'),
('MER-104', 1, 'Mercedes-Benz', 'C200', 48000, 'AVAILABLE'),
('HYU-105', 3, 'Hyundai', 'Tucson', 31000, 'AVAILABLE'),
('KIA-106', 2, 'Kia', 'Ceed Sportswagon', 54000, 'AVAILABLE'),
('MAZ-107', 7, 'Mazda', 'MX-5', 22000, 'AVAILABLE'),
('VOL-108', 3, 'Volvo', 'XC60', 68000, 'AVAILABLE'),
('REN-109', 4, 'Renault', 'Master', 165000, 'AVAILABLE'),
('POR-110', 5, 'Porsche', '911 Carrera', 18000, 'AVAILABLE');

INSERT INTO rental (car_id, customer_id, from_date, to_date, return_date, rental_fee) VALUES
(1, 1, CURRENT_DATE - INTERVAL '15 day', CURRENT_DATE - INTERVAL '10 day', CURRENT_DATE - INTERVAL '10 day', 40000.00),
(7, 2, CURRENT_DATE - INTERVAL '2 day', CURRENT_DATE + INTERVAL '3 day', NULL, NULL);

INSERT INTO service_log (car_id, from_date, to_date, service_description, service_fee) VALUES
(4, CURRENT_DATE - INTERVAL '1 day', CURRENT_DATE + INTERVAL '4 day', 'Olajcsere és fékbetét javítás', 45000.00);
