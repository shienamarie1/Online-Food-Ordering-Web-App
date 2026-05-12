<?php
// Ensure the user is logged in before querying wallet data.
if (!isset($_SESSION['user_id']) || empty($_SESSION['user_id'])) {
    $balance = "0.00";
    return;
}

$user_id = intval($_SESSION['user_id']);
$sql = $con->query("SELECT * FROM wallet WHERE user_id = $user_id");
$row1 = $sql ? $sql->fetch_assoc() : null;
if (!$row1 || !isset($row1['id'])) {
    $balance = "0.00";
    return;
}

$wallet_id = intval($row1['id']);
$sql = $con->query("SELECT * FROM wallet_details WHERE wallet_id = $wallet_id");
$row1 = $sql ? $sql->fetch_assoc() : null;
$balance = ($row1 && isset($row1['balance'])) ? $row1['balance'] : "0.00";
?>