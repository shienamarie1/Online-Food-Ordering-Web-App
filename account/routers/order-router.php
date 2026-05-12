<?php
include '../includes/connect.php';

$address = isset($_POST['address']) ? htmlspecialchars($_POST['address']) : '';
$description = isset($_POST['description']) ? htmlspecialchars($_POST['description']) : '';
$payment_type = isset($_POST['payment_type']) ? $_POST['payment_type'] : 'Wallet';
$total = isset($_POST['total']) ? floatval($_POST['total']) : 0;

// collect items from numeric POST keys
$items = [];
foreach ($_POST as $key => $value) {
	if (is_numeric($key)) {
		$items[] = [
			'item_id' => intval($key),
			'quantity' => intval($value)
		];
	}
}

// build payload
$payload = [
	'customer_id' => isset($_SESSION['user_id']) ? intval($_SESSION['user_id']) : null,
	'payment_type' => $payment_type,
	'address' => $address,
	'total' => $total,
	'description' => $description,
	'items' => $items
];

$nodeUrl = 'http://127.0.0.1:3000/orders/create';
$ch = curl_init($nodeUrl);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($payload));
curl_setopt($ch, CURLOPT_HTTPHEADER, [
	'Content-Type: application/json'
]);
curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 4);
curl_setopt($ch, CURLOPT_TIMEOUT, 10);
$resp = @curl_exec($ch);
$http = curl_getinfo($ch, CURLINFO_HTTP_CODE);
$err = curl_error($ch);
curl_close($ch);

if ($resp && $http === 200) {
	$data = json_decode($resp, true);
	if (isset($data['success']) && $data['success'] === true) {
		header('Location: ../orders.php');
		exit();
	}
}

// fallback: if API failed, redirect back to orders with error
header('Location: ../orders.php');
?>