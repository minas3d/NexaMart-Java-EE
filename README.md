# 🛒 NexaMart

An online electronics store built on classic Java EE (Servlets, JSP, JDBC,
PostgreSQL) — no Spring, no Hibernate, no high-level framework. The goal is
to show the engineering that frameworks usually abstract away: the Servlet
lifecycle, HTTP handling, session management, transactions, connection
pooling, and concurrency safety.

---

## 📑 Table of Contents

- [Project Overview](#-project-overview)
- [Main Features](#-main-features)
- [Architecture](#%EF%B8%8F-architecture)
- [Technology Stack](#%EF%B8%8F-technology-stack)
- [Database Design](#%EF%B8%8F-database-design)
- [Project Structure](#-project-structure)
- [Security](#-security)
- [Getting Started](#-getting-started)
- [Running in IntelliJ IDEA](#-running-in-intellij-idea)
- [Test Accounts](#-test-accounts)

---

## 🎯 Project Overview

NexaMart is an online store selling laptops, phones, audio gear, smart home
devices, gaming accessories, and everyday tech essentials. It covers the
full retail flow:

- Customers can register, browse the catalog, search and filter by
  category, manage a cart (as a guest or signed in), check out, and track
  their order history.
- Admins have a full back office: a dashboard with store stats, product
  CRUD (with image upload), category management, order management with
  status updates, and a customer directory.

---

## ✨ Main Features

**Customers**
- Registration and login with session-based authentication
- Browse products with search, category filter, and pagination
- Product detail pages with stock-aware "Add to Cart"
- Shopping cart that works for guests and merges into the account on login
- Transactional checkout (stock is only decremented if the order succeeds)
- Order history and order confirmation pages
- Editable profile with a profile picture
- "Forgot password" flow with emailed reset links

**Admins**
- Dashboard with product/category/order/customer counts, pending orders,
  and total revenue
- Product management: create, edit, delete, upload images
- Category management: create, edit, delete
- Order management: view all orders, inspect line items, update status
- Customer directory

---

## 🏗️ Architecture

```
Browser → JSP → Servlet → Service → DAO → JDBC → PostgreSQL
```

- **JSP** renders HTML only; no business logic or SQL lives in a JSP.
- **Servlets** handle HTTP requests/responses and delegate to services.
- **Services** hold validation and business rules.
- **DAOs** are the only classes that touch JDBC.
- **Filters** (`EncodingFilter`, `LoggingFilter`, `AuthFilter`,
  `AdminFilter`) run in front of the servlets to normalize encoding, log
  requests, and guard customer-only / admin-only pages.

---

## ⚙️ Technology Stack

- Java 17+ (Jakarta Servlet API, Jakarta Mail)
- JSP (no JSTL/EL frameworks — plain scriptlets, by design)
- JDBC with [HikariCP](https://github.com/brettwooldridge/HikariCP) for
  connection pooling
- PostgreSQL
- [jBCrypt](https://www.mindrot.org/projects/jBCrypt/) for password hashing
- Apache Tomcat (any Jakarta EE 10 compatible servlet container)

All third-party jars are already in `src/main/webapp/WEB-INF/lib/`, and a
`pom.xml` is included so IDEs resolve them automatically for compilation.
The jars are what actually ship in the WAR at runtime — the POM's
dependencies are all `provided` scope on purpose, so Maven doesn't double
up what's already sitting in `WEB-INF/lib/`.

---

## 🗄️ Database Design

| Table | Purpose |
|---|---|
| `users` | Login credentials and role (`CUSTOMER` / `ADMIN`) |
| `customers` | Customer profile, linked 1-to-1 with `users` |
| `categories` | Product categories |
| `products` | Catalog items, linked to a category |
| `orders` | One row per placed order |
| `order_items` | Line items for each order (price is frozen at order time) |
| `cart_items` | Persisted cart for logged-in customers |
| `password_reset_tokens` | Single-use, expiring tokens for password resets |

Full definitions, constraints, and indexes are in
[`database/1-schema.sql`](database/1-schema.sql). Sample categories and 24
products are in [`database/2-seed-data.sql`](database/2-seed-data.sql).

---

## 📁 Project Structure

```
NexaMart-Java-EE/
├── database/
│   ├── 1-schema.sql
│   └── 2-seed-data.sql
├── src/main/
│   ├── java/nti/
│   │   ├── dao/            JDBC data access classes
│   │   ├── exceptions/     BusinessException
│   │   ├── filters/        Encoding, Logging, Auth, Admin filters
│   │   ├── listeners/      Connection pool startup/shutdown
│   │   ├── models/         Plain Java objects
│   │   ├── services/       Business logic
│   │   ├── servlets/
│   │   │   ├── admin/      Admin dashboard, products, categories, orders, customers
│   │   │   ├── auth/       Register, login, logout, password reset
│   │   │   ├── common/     Home, image serving
│   │   │   └── customer/   Browse, cart, checkout, orders, profile
│   │   └── utils/          DB connection, password hashing, validation, mail, tokens
│   ├── resources/
│   │   └── mail.properties.example
│   └── webapp/
│       ├── admin/          Admin JSPs
│       ├── customer/       Customer-account JSPs
│       ├── css/, js/, images/
│       └── WEB-INF/
│           ├── web.xml
│           └── partials/   header, footer, admin sidebar, flash messages
└── README.md
```

---

## 🔐 Security

- Passwords are hashed with bcrypt (jBCrypt); never stored or logged in
  plain text, never put in cookies.
- All SQL uses `PreparedStatement` — no string-concatenated queries.
- `AuthFilter` blocks anonymous access to customer-only pages
  (dashboard, orders, profile, checkout, order confirmation).
- `AdminFilter` blocks anyone who isn't logged in as `ADMIN` from every
  `/admin/*` URL, JSPs included.
- Session-based authentication; logging out invalidates the session.

---

## 🚀 Getting Started

### 1. Prerequisites

- JDK 17 or newer
- PostgreSQL 13+
- Apache Tomcat 10+ (Jakarta EE 10 — **not** Tomcat 9, which uses the old
  `javax.*` namespace)
- IntelliJ IDEA (Ultimate, for the built-in Java EE / Web support) or any
  IDE that can deploy a WAR to Tomcat

### 2. Create the database

```bash
createdb nexamart_db
psql -U postgres -d nexamart_db -f database/1-schema.sql
psql -U postgres -d nexamart_db -f database/2-seed-data.sql
```

The connection URL is configured in
`src/main/webapp/WEB-INF/web.xml` as a `<context-param>`:

```xml
<param-name>db.url</param-name>
<param-value>jdbc:postgresql://localhost:5432/nexamart_db</param-value>
```

Update the username/password context-params in the same file to match your
local PostgreSQL setup.

### 3. (Optional) Configure outgoing mail

Password-reset emails need SMTP credentials. Copy the example file and
fill in your own values:

```bash
cp src/main/resources/mail.properties.example src/main/resources/mail.properties
```

If you skip this step, everything except "Forgot password" still works.

---

## 🖥️ Running in IntelliJ IDEA

### Option A — Maven (recommended)

The project ships with a `pom.xml`, so IntelliJ can set almost everything
up automatically:

1. **File → Open** and select the project's root folder. IntelliJ will
   detect the `pom.xml` and offer to **load it as a Maven project** —
   accept it (or open it manually from the Maven tool window if it
   doesn't prompt you).
2. **File → Project Structure → Project** — set the Project SDK to
   Java 17+.
3. Let Maven finish resolving dependencies (bottom status bar). This
   alone fixes `package jakarta.servlet does not exist` and similar
   errors, since the servlet API is now a normal (compile-time)
   dependency.
4. **File → Project Structure → Facets** — you should see a **Web**
   facet auto-created from the `maven-war-plugin` config, pointed at
   `src/main/webapp`. If it's missing, add it manually
   (**+ → Web**) and point it there.
5. **Run → Edit Configurations → + → Tomcat Server → Local**, point it
   at your local Tomcat 10.1+ install. Under **Deployment**, add the
   artifact named `nexamart:war exploded` (Maven creates this
   automatically once the POM is loaded) rather than building one by
   hand.
6. Run the configuration. The app will be available at:

   ```
   http://localhost:8080/NexaMart/
   ```

   (rename the deployed context path under the **Deployment** tab of the
   run configuration if you want something other than `/NexaMart`).

### Option B — No Maven

If you'd rather not use Maven, the project still works without it, since
every dependency is already a physical jar in
`src/main/webapp/WEB-INF/lib/`:

1. **File → Open** and select the project's root folder. If IntelliJ
   offers to import the `pom.xml`, decline (**Open as Project** instead
   of **Open as Maven Project**), or just delete `pom.xml` locally.
2. **File → Project Structure → Project** — set the Project SDK to
   Java 17+.
3. **File → Project Structure → Modules** — add a **Web** facet (or
   **Add Framework Support → Web Application**), and point its web
   resource directory at `src/main/webapp`.
4. Still in Modules, open the **Dependencies** tab and make sure every
   jar under `src/main/webapp/WEB-INF/lib/` is added as a module
   dependency (IntelliJ usually auto-detects jars placed in
   `WEB-INF/lib`; if it doesn't, add them manually with
   **+ → JARs or Directories**). This is also what fixes
   `package jakarta.servlet does not exist` on this path — that class
   comes from the Application Server library, so also add your Tomcat
   installation as a dependency: **Settings → Build, Execution,
   Deployment → Application Servers → + → Tomcat Server**, then on the
   module's **Dependencies** tab, **+ → Library... → Application Server
   Library...** and pick it.
5. **Run → Edit Configurations → + → Tomcat Server → Local**, point it at
   your local Tomcat install, and under **Deployment** add this
   project's artifact (an "exploded WAR" is fine for development).
6. Run the configuration — same URL as above.

---

## 👤 Test Accounts

The seed data only loads categories and products — no user accounts,
since passwords must be bcrypt-hashed and can't be safely hard-coded in a
plain SQL file.

**Customer account:** just register one at `/register.jsp`.

**Admin account:** register a normal account, then promote it from `psql`:

```sql
UPDATE users SET role = 'ADMIN' WHERE username = 'your_username';
```

Log out and back in, and you'll see an **Admin Panel** link in the
navigation bar.

---

## 📦 Deliverables Checklist

- ✅ Source code (`src/`)
- ✅ Database script (`database/1-schema.sql`, `database/2-seed-data.sql`)
- ✅ README with setup instructions, and test-account instructions above
- ⬜ ER diagram — not included; the table relationships are documented in
  [Database Design](#%EF%B8%8F-database-design) above
- ⬜ Screenshots — not included; the UI changed significantly in this
  revision, so prior screenshots were removed rather than left stale
