INSERT INTO categories (name, description) VALUES
('Laptops & Computers', 'Laptops, ultrabooks, and creator machines for work and play'),
('Smartphones & Tablets', 'Phones and tablets for everyday life'),
('Audio & Headphones', 'Headphones, earbuds, and speakers'),
('Smart Home', 'Connected devices that make your home smarter'),
('Gaming', 'Keyboards, mice, monitors, and controllers for gamers'),
('Accessories', 'Chargers, cases, and everyday essentials');

INSERT INTO products (category_id, name, description, price, stock) VALUES
(1, 'Nexa Pro 15 Laptop', '15.6" FHD display, 16GB RAM, 512GB SSD. A reliable all-rounder for work and study.', 899.99, 25),
(1, 'AeroBook Air 13', 'Ultra-light 13.3" laptop at just 1.1kg, 8GB RAM, 256GB SSD. Built for life on the move.', 649.00, 40),
(1, 'Vortex Gaming Laptop 16', '16" 144Hz display, dedicated GPU, 16GB RAM, 1TB SSD. Serious power for serious games.', 1399.00, 12),
(1, 'StudioBook 14 Creator Edition', 'Color-accurate 14" display, 16GB RAM, 512GB SSD. Made for designers and editors.', 1099.50, 18),

(2, 'Nexa Phone 12', '6.5" AMOLED display, 128GB storage, triple camera system.', 599.00, 60),
(2, 'Nexa Phone 12 Lite', '6.1" display, 64GB storage. All the essentials at a friendly price.', 349.00, 80),
(2, 'OrbitTab 11', '11" tablet with stylus support and 128GB storage, perfect for notes and streaming.', 429.00, 35),
(2, 'OrbitTab Mini 8', 'Compact 8" tablet with 64GB storage, light enough for one-handed use.', 249.00, 45),

(3, 'SonicWave Pro ANC Headphones', 'Over-ear headphones with active noise cancelling and 30-hour battery life.', 179.99, 50),
(3, 'EchoBuds True Wireless Earbuds', 'True wireless earbuds with ANC and up to 30 hours of combined battery life.', 99.99, 90),
(3, 'BassLine Portable Speaker', 'Waterproof (IPX7) Bluetooth speaker with 12-hour battery and deep bass.', 59.99, 70),
(3, 'StageSound Soundbar 2.1', 'Home theater soundbar with wireless subwoofer for richer movie nights.', 149.00, 20),

(4, 'HomeHub Smart Display 7"', 'Voice-controlled smart display that manages your connected home from one screen.', 89.99, 30),
(4, 'GlowPoint Smart Bulb (4-Pack)', 'Color-changing WiFi smart bulbs, no hub required, 4 bulbs per pack.', 34.99, 100),
(4, 'SecureEye Indoor Camera', '1080p WiFi security camera with night vision and motion alerts.', 44.99, 55),
(4, 'ClimateSense Smart Thermostat', 'WiFi programmable thermostat that learns your schedule and saves energy.', 119.00, 25),

(5, 'Vortex Mechanical Keyboard RGB', 'Hot-swappable mechanical keyboard with per-key RGB backlighting.', 89.99, 40),
(5, 'Vortex Wireless Gaming Mouse', '16,000 DPI wireless mouse with low-latency connection for competitive play.', 59.99, 50),
(5, 'GridView 27" Gaming Monitor', '27" QHD monitor, 165Hz refresh rate, 1ms response time.', 329.00, 15),
(5, 'PulseGrip Pro Controller', 'Wireless controller compatible with PC and major consoles.', 49.99, 60),

(6, 'PowerCore 20K Power Bank', '20,000mAh portable charger with fast-charging support for phones and tablets.', 39.99, 85),
(6, 'SnapCharge Wireless Charging Pad', '15W fast wireless charging pad, compatible with Qi-enabled devices.', 24.99, 95),
(6, 'TravelHub 7-in-1 USB-C Adapter', 'Compact USB-C hub with HDMI, USB-A, SD card, and fast-charge passthrough.', 44.99, 65),
(6, 'GuardCase Shockproof Phone Case', 'Drop-tested protective case with raised edges for screen and camera protection.', 19.99, 120);
