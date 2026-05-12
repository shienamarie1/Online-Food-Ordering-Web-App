<?php
include '../includes/connect.php';

// Delegate authentication to the Node backend so PHP sessions are created
// only when the Node API validates credentials.
$username = isset($_POST['username']) ? $_POST['username'] : '';
$password = isset($_POST['password']) ? $_POST['password'] : '';

// Call Node /login
$nodeUrl = 'http://127.0.0.1:3000/login';
$ch = curl_init($nodeUrl);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query(['username'=>$username, 'password'=>$password]));
curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 2);
curl_setopt($ch, CURLOPT_TIMEOUT, 4);
$resp = @curl_exec($ch);
$httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
$curlErr = curl_error($ch);
curl_close($ch);

if ($resp && $httpCode === 200) {
	$data = json_decode($resp, true);
	if (isset($data['success']) && $data['success'] === true && isset($data['user'])) {
		$user = $data['user'];
		$user_id = isset($user['id']) ? $user['id'] : null;
		$name = isset($user['name']) ? $user['name'] : '';
		$role = isset($user['role']) ? $user['role'] : '';

		session_start();
		if (strtolower($role) === 'administrator') {
			$_SESSION['admin_sid'] = session_id();
		} else {
			$_SESSION['customer_sid'] = session_id();
		}
		$_SESSION['user_id'] = $user_id;
		$_SESSION['role'] = $role;
		$_SESSION['name'] = $name;

		if (strtolower($role) === 'administrator') {
			header("location: ../admin-page.php");
			exit();
		} else {
			header("location: ../index.php");
			exit();
		}
	}
}

// Fallback: on any failure, redirect back to login
header("location: ../login.php");
?>