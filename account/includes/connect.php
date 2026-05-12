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
    $result = false;

    if (function_exists('curl_version')) {
        $ch = curl_init($url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 1);
        curl_setopt($ch, CURLOPT_TIMEOUT, 1);
        curl_setopt($ch, CURLOPT_HTTPHEADER, ["Accept: application/json"]);
        $result = @curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        curl_close($ch);
        if ($httpCode !== 200) {
            return false;
        }
    } else {
        $options = [
            'http' => [
                'method' => 'GET',
                'timeout' => 1,
                'header' => "Accept: application/json\r\n"
            ]
        ];
        $context = stream_context_create($options);
        $result = @file_get_contents($url, false, $context);
    }

    if (!$result) {
        return false;
    }

    $data = @json_decode($result, true);
    return is_array($data) && isset($data['success']) && $data['success'] === true;
}

if (!isNodeServerAlive()) {
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