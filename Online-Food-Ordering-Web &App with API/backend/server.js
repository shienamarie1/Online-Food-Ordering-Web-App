const express = require("express");
const mysql = require("mysql");
const cors = require("cors");

const app = express();

app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// DB
const db = mysql.createConnection({
  host: "localhost",
  user: "root",
  password: "",
  database: "foods"
});

db.connect(err => {
  if (err) console.log("DB Error:", err);
  else console.log("DB Connected");
});

// =======================
// WALLET FUNCTION
// =======================
function addWallet(db, user_id, amount) {
  const sql = `
    SELECT w.id AS wallet_id, wd.balance
    FROM wallet w
    JOIN wallet_details wd ON w.id = wd.wallet_id
    WHERE w.user_id = ?
  `;

  db.query(sql, [user_id], (err, result) => {
    if (err || result.length === 0) return;

    const wallet_id = result[0].wallet_id;
    const newBalance = Number(result[0].balance) + Number(amount);

    db.query(
      "UPDATE wallet_details SET balance=? WHERE wallet_id=?",
      [newBalance, wallet_id]
    );
  });
}// =======================
// BONUS CHECK FUNCTION
// =======================
function checkBonus(rider_id) {

  db.query(
    "SELECT COUNT(*) as cnt FROM orders WHERE rider_id=? AND status='Delivered'",
    [rider_id],
    (err, rows) => {

      if (err || !rows.length) return;

      const count = rows[0].cnt;

      if (count > 0 && count % 10 === 0) {
        addWallet(db, rider_id, 5000);
      }
    }
  );
}
// =======================
// REGISTER RIDER
// =======================
app.post("/register", (req, res) => {

  const { name, username, password, role } = req.body;
  const userRole = role || 'customer';

  const sql = `
    INSERT INTO users (name, username, password, role)
    VALUES (?, ?, ?, ?)
  `;

  db.query(sql, [name, username, password, userRole], (err, result) => {

    if (err) return res.json({ success: false, message: err.message });

    const userId = result.insertId;

    db.query("INSERT INTO wallet (user_id) VALUES (?)", [userId], (err2, w) => {

      if (err2) return res.json({ success: false });

      db.query(
        "INSERT INTO wallet_details (wallet_id, balance) VALUES (?, 0)",
        [w.insertId],
        () => {
          res.json({ success: true, userId: userId, role: userRole });
        }
      );
    });
  });
});
app.get("/wallet/:id", (req, res) => {

  const sql = `
    SELECT wd.balance
    FROM wallet w
    JOIN wallet_details wd ON w.id = wd.wallet_id
    WHERE w.user_id = ?
  `;

  db.query(sql, [req.params.id], (err, result) => {

    if (err || result.length === 0) {
      return res.json({ balance: "0.00" });
    }

    res.json({ balance: result[0].balance });
  });
});

// =======================
// LOGIN
// =======================
app.post("/login", (req, res) => {

  const { username, password } = req.body;

  const sql = "SELECT * FROM users WHERE username=? AND password=?";

  db.query(sql, [username, password], (err, result) => {

    if (err) {
      return res.json({ success: false, message: err.message });
    }

    if (result.length > 0) {

      const user = result[0];

      // 🔥 BLOCK UNVERIFIED USERS HERE
      if (user.verified == 0 || user.verified == "pending") {
        return res.json({
          success: false,
          message: "Account not verified yet"
        });
      }

      return res.json({
        success: true,
        user: user
      });

    } else {
      res.json({
        success: false,
        message: "Invalid login"
      });
    }
  });
});

app.get("/health", (req, res) => {
  res.json({ success: true, status: "ok" });
});

app.get("/user/:id", (req, res) => {

  const id = req.params.id;

  const sql = "SELECT name, email, contact FROM users WHERE id=?";

  db.query(sql, [id], (err, result) => {

    if (err) return res.json({ success:false, message: err.message });

    if (result.length > 0) {
      result[0].wallet = "0.00";
      res.json(result[0]);
    } else {
      res.json(null);
    }
  });
});

// =======================
// GET AVAILABLE ORDERS
// =======================
app.get("/orders/available", (req, res) => {

  db.query(
    "SELECT * FROM orders WHERE rider_id IS NULL",
    (err, result) => {
      if (err) return res.json(err);
      res.json(result);
    }
  );
});


// =======================
// ASSIGN RIDER
// =======================
app.post("/orders/assign", (req, res) => {

  const { order_id, rider_id } = req.body;

  db.query(
    "UPDATE orders SET rider_id=?, status='Assigned' WHERE id=?",
    [rider_id, order_id],
    (err) => {
      if (err) return res.json({ success: false });
      res.json({ success: true });
    }
  );
});


// =======================
// UPDATE STATUS
// =======================
app.post("/orders/update-status", (req, res) => {

  const { order_id, status, rider_id } = req.body;

  db.query(
    "UPDATE orders SET status=? WHERE id=?",
    [status, order_id],
    (err) => {

      if (err) {
        return res.json({ success: false, message: err.message });
      }

      // ONLY RUN IF DELIVERED
      if (status === "Delivered") {

  db.query(
    "SELECT COUNT(*) as cnt FROM orders WHERE rider_id=? AND status='Delivered'",
    [rider_id],
    (err, rows) => {

      if (err || !rows.length) return;

      const count = rows[0].cnt;

      console.log("Delivered:", count);

      db.query(
        "SELECT last_bonus_count FROM users WHERE id=?",
        [rider_id],
        (err2, user) => {

          if (err2 || !user.length) return;

          const lastBonus = user[0].last_bonus_count || 0;

          // 🎯 ONLY GIVE BONUS IF +10 NEW DELIVERIES
          if (count - lastBonus >= 10) {

            console.log("🎉 BONUS GIVEN");

            // add wallet
            db.query(
              "UPDATE wallet_details wd JOIN wallet w ON wd.wallet_id=w.id SET wd.balance = wd.balance + 5000 WHERE w.user_id=?",
              [rider_id]
            );

            // update tracker
            db.query(
              "UPDATE users SET last_bonus_count=? WHERE id=?",
              [count, rider_id]
            );
          }
        }
      );
    }
  );
}

      res.json({ success: true });
    }
  );
});

// =======================
// RIDER ORDERS (FIXED)
// =======================
app.get("/rider/orders/:id", (req, res) => {

  const riderId = req.params.id;

const sql = `
  SELECT 
    o.id,
    o.customer_id,
    o.rider_id,
    o.address,
    o.total,
    o.status,
    o.date,
    o.payment_type,
    u.name AS customer_name,
     u.contact
  FROM orders o
  
  LEFT JOIN users u ON o.customer_id = u.id
  WHERE o.rider_id = ?
`;
  db.query(sql, [riderId], (err, result) => {

    if (err) {
      console.log("SQL ERROR:", err);
      return res.status(500).json({
        success: false,
        message: err.message
      });
    }

    res.json(result);
  });
});
app.listen(3000, () => {
  console.log("Server running on port 3000");
});
