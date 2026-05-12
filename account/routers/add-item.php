<?php
include '../includes/connect.php';

$name = $_POST['name'];
$price = $_POST['price'];
$stock = isset($_POST['stock']) ? intval($_POST['stock']) : 0;
$image = addslashes(file_get_contents($_FILES['image']['tmp_name']));
$sql = "INSERT INTO items (name, price, stock, image) VALUES ('$name', $price, $stock, '$image')";
$con->query($sql);
if(!$con)
{
    echo mysqli_error($con);
}
var_dump($con->error);
 header("location: ../admin-page.php");
?>