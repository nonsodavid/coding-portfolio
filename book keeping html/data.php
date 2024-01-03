<?php // connect.php allows connection to the database

  require 'connect.php'; //using require will include the connect.php file each time it is called.

    if (isset($_POST['id'])   &&
        isset($_POST['title']) &&
        isset($_POST['author'])
        
		)
		
      
  {
    $id     = assign_data($conn, 'id');
    $title  = assign_data($conn, 'title');
    $author = assign_data($conn, 'author');
   
    
	$query    = "INSERT INTO (your table) VALUES ('$id', '$title', '$author')";
	
    $result   = $conn->query($query);
    if (!$result) echo "<br><br>INSERT failed: $query<br>" .
	
      $conn->error . "<br><br>";
  }


echo<<<_HTML

  <form action="  " method="post">
  
    Book id <input type="text" name="id"> <br><br>
    Book title <input type="text" name="title"> <br><br>
    Author name <input type="text" name="author"> <br><br>
      
    <input type="submit" value="ADD RECORD">
	
   </form>
_HTML;
  
  
  
  function assign_data($conn, $var)
  {
    return $conn->real_escape_string($_POST[$var]);
  }
  
  
  ?>

