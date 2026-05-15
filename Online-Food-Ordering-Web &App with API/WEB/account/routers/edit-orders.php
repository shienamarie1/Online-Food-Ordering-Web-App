<?php
include '../includes/connect.php';

$id = $_POST['id'];
$status = $_POST['status'];

$rider_id = isset($_POST['rider_id']) && $_POST['rider_id'] != '' 
            ? $_POST['rider_id'] 
            : NULL;

mysqli_query($con, "UPDATE orders SET 
status='$status',
rider_id=".($rider_id ? "'$rider_id'" : "NULL")."
WHERE id='$id'");

header("location: ../all-orders.php");
?>