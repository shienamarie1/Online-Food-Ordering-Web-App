<?php
header('Content-Type: application/json; charset=utf-8');

$url = 'http://127.0.0.1:3000/health';
$ch = curl_init($url);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 2);
curl_setopt($ch, CURLOPT_TIMEOUT, 3);
$body = @curl_exec($ch);
$code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
$err = curl_error($ch);
curl_close($ch);

$alive = false;
if ($body !== false && ($code === 200 || $code === 204 || $code === 301 || $code === 302)) {
    $alive = true;
}

echo json_encode([
    'alive' => $alive,
    'http_code' => (int)$code,
    'body' => $body ?? null,
    'error' => $err ?: null
]);

?>
