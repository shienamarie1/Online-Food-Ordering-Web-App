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
    <p>The account system is currently unavailable because the server is not responding.</p>
    <div style="display:flex;gap:12px;align-items:center;justify-content:center;">
      <button id="tryBtn" style="padding:12px 24px;border-radius:999px;background:#111;color:#fff;border:0;cursor:pointer;font-weight:600;">Try Again</button>
      <span id="status" style="color:#666;font-size:0.95rem;"></span>
    </div>
  </div>
  <script>
    const btn = document.getElementById('tryBtn');
    const status = document.getElementById('status');

    function setStatus(text, temporary = true){
      status.textContent = text;
      if (temporary) setTimeout(()=>{ status.textContent = ''; }, 3500);
    }

    async function checkAndRedirect(){
      setStatus('Checking server...', false);
      try {
        const res = await fetch('test_node_health.php', { cache: 'no-store' });
        if (!res.ok) throw new Error('proxy-failed');
        const data = await res.json();
        if (data.alive) {
          // Node server is responding — go to login
          window.location.href = 'login.php';
          return;
        }
        setStatus('Still offline — server not responding.');
      } catch (e) {
        setStatus('Still offline — unable to reach server.');
      }
    }

    btn.addEventListener('click', () => {
      checkAndRedirect();
    });

    // optional: try once on load
    window.addEventListener('load', () => {
      // small delay so message is visible
      setTimeout(() => { checkAndRedirect(); }, 600);
    });
  </script>
</body>
</html>
