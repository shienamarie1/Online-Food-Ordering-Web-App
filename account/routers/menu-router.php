<?php
include '../includes/connect.php';

foreach ($_POST as $key => $value)
{
    // Update Name
    if(preg_match("/[0-9]+_name/", $key))
    {
        if($value != '')
        {
            $id = strtok($key, '_');
            $value = mysqli_real_escape_string($con, htmlspecialchars($value));

            $sql = "UPDATE items 
                    SET name='$value' 
                    WHERE id=$id";

            $con->query($sql);
        }
    }

    // Update Price
    if(preg_match("/[0-9]+_price/", $key))
    {
        $id = strtok($key, '_');

        $sql = "UPDATE items 
                SET price='$value' 
                WHERE id=$id";

        $con->query($sql);
    }

    // Update Stock
    if(preg_match("/[0-9]+_stock/", $key))
    {
        $id = strtok($key, '_');

        $sql = "UPDATE items 
                SET stock='$value' 
                WHERE id=$id";

        $con->query($sql);
    }

    // Hide / Show Item
    if(preg_match("/[0-9]+_hide/", $key))
    {
        $id = strtok($key, '_');

        if($_POST[$key] == 1)
        {
            $sql = "UPDATE items 
                    SET deleted=0 
                    WHERE id=$id";
        }
        else
        {
            $sql = "UPDATE items 
                    SET deleted=1 
                    WHERE id=$id";
        }

        $con->query($sql);
    }
}


// IMAGE UPLOAD
foreach ($_FILES as $key => $file)
{
    if(preg_match("/[0-9]+_image/", $key))
    {
        $id = strtok($key, '_');

        if(!empty($file['tmp_name']))
        {
            $image = addslashes(file_get_contents($file['tmp_name']));

            $sql = "UPDATE items 
                    SET image='$image' 
                    WHERE id=$id";

            $con->query($sql);
        }
    }
}

header("location: ../admin-page.php");
?>