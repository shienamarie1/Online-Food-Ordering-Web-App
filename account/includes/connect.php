<?php
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

function redirectToMaintenance() {
    $baseDir = dirname($_SERVER['SCRIPT_NAME']);
    $baseDir = preg_replace('#/routers(/.*)?$#', '', $baseDir);
    $maintenanceUrl = $baseDir . '/maintenance.php';
    header('Location: ' . $maintenanceUrl);
    exit();
}

$offlineFlag = __DIR__ . '/../offline.flag';
if (file_exists($offlineFlag)) {
    redirectToMaintenance();
}

function isNodeServerAlive() {
    $url = 'http://127.0.0.1:3000/health';
    $logFile = __DIR__ . '/../debug_health.log';
    $entry = [
        'time' => date('c'),
        'url' => $url,
        'method' => null,
        'http_code' => null,
        'response' => null,
        'error' => null,
    ];

    // Try cURL first
    if (function_exists('curl_version')) {
        $entry['method'] = 'curl';
        $ch = curl_init($url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 2);
        curl_setopt($ch, CURLOPT_TIMEOUT, 2);
        curl_setopt($ch, CURLOPT_HTTPHEADER, ["Accept: application/json"]);
        $result = @curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $curlErr = curl_error($ch);
        curl_close($ch);

        $entry['http_code'] = $httpCode;
        $entry['response'] = $result;
        $entry['error'] = $curlErr ?: null;

        file_put_contents($logFile, json_encode($entry) . PHP_EOL, FILE_APPEND);

        return ($httpCode === 200 && $result);
    }

    // Fallback to file_get_contents (allow_url_fopen must be enabled)
    $entry['method'] = 'file_get_contents';
    $options = [
        'http' => [
            'method' => 'GET',
            'timeout' => 2,
            'header' => "Accept: application/json\r\n"
        ]
    ];
    $context = stream_context_create($options);
    $result = @file_get_contents($url, false, $context);
    $entry['response'] = $result;
    file_put_contents($logFile, json_encode($entry) . PHP_EOL, FILE_APPEND);

    return ($result !== false);
}

if (!isNodeServerAlive()) {
    // write a short note to the debug log for visibility
    $note = date('c') . " - Node healthcheck failed; redirecting to maintenance" . PHP_EOL;
    @file_put_contents(__DIR__ . '/../debug_health.log', $note, FILE_APPEND);
    redirectToMaintenance();
}

$servername = "127.0.0.1";
$server_user = "root";
$server_pass = "";
$dbname = "foods";
$name = isset($_SESSION['name']) ? $_SESSION['name'] : null;
$role = isset($_SESSION['role']) ? $_SESSION['role'] : null;

$con = @new mysqli($servername, $server_user, $server_pass, $dbname);
if ($con->connect_errno) {
    redirectToMaintenance();
}
?>