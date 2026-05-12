<?php
session_start();
$offlineFlag = __DIR__ . '/../offline.flag';
if (file_exists($offlineFlag)) {
    $maintenanceUrl = preg_replace('#/routers(/.*)?$#', '', dirname($_SERVER['SCRIPT_NAME'])) . '/maintenance.php';
    header("Location: $maintenanceUrl");
    exit();
}
$servername = "127.0.0.1";
$server_user = "root";
$server_pass = "";
$dbname = "foods";
$name = isset($_SESSION['name']) ? $_SESSION['name'] : null;
$role = isset($_SESSION['role']) ? $_SESSION['role'] : null;
$con = new mysqli($servername, $server_user, $server_pass, $dbname);
?>