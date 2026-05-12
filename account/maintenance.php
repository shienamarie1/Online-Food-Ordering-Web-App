<?php
// Simple offline maintenance page for the account system.
?>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>System Offline</title>
  <style>
    body {
      margin: 0;
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #fff;
      color: #111;
      font-family: Arial, Helvetica, sans-serif;
    }
    .offline-card {
      max-width: 520px;
      width: 100%;
      padding: 32px;
      border: 1px solid #ddd;
      border-radius: 20px;
      box-shadow: 0 18px 40px rgba(0,0,0,0.08);
      background: #fff;
      text-align: center;
    }
    .offline-card h1 {
      margin: 0 0 16px;
      font-size: 2rem;
      letter-spacing: -0.02em;
    }
    .offline-card p {
      margin: 0 0 24px;
      color: #444;
      line-height: 1.6;
    }
    .offline-card a {
      display: inline-block;
      padding: 12px 24px;
      border-radius: 999px;
      background: #111;
      color: #fff;
      text-decoration: none;
      font-weight: 600;
    }
  </style>
</head>
<body>
  <div class="offline-card">
    <h1>System Offline</h1>
    <p>The account system is currently unavailable because the server has been turned off.</p>
    <a href="javascript:location.reload();">Try Again</a>
  </div>
</body>
</html>
