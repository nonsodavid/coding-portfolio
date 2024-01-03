<html>
<head>
	<meta charset = "utf-8">
	<style type="text/css">
		td, th {border:1px solid black;}
	</style>
</head>


<body>

<?php // connect.php allows connection to the database

  require_once 'connect.php'; //using require will include the connect.php file each time it is called.

    if (isset($_POST['id'])   &&
        isset($_POST['title']) &&
        isset($_POST['author']) 
    )
  {
    $id     = assign_data($conn, 'id');
    $title  = assign_data($conn, 'title');
    $author = assign_data($conn, 'author');
   
    
    // No validation is performed so far
	$query    = "INSERT INTO (your table name) VALUES ('$id', '$title', '$author')";
		
    $result   = $conn->query($query);
    if (!$result) echo "<br><br>INSERT failed: $query<br>" .
	
      $conn->error . "<br><br>";
  }

//  else {
//	  print "<br><br> You did not fill all required <br><br>";
//  }



print<<<_HTML
   <form action=" " method="post">
  
    Book id:     <input type="text" name="id" value = ""> <br><br>
    Book title:  <input type="text" name="title" value = ""> <br><br>
    Author name: <input type="text" name="author" value = ""> <br><br>
      
    <input type="submit" value="add record">
	
   </form>
_HTML;
  
  
  function assign_data($conn, $var)
  {
    return $conn->real_escape_string($_POST[$var]);
  }
  
  
  $query  = "SELECT * FROM (your table name)";
  $result = $conn->query($query);
  if (!$result) die ("Database access failed: " . $conn->error);

  $rows = $result->num_rows;


print<<<_HTML
   <b>Here is your Books list</b>
	
	<table>
		  <tr>
			<th>Book id</th>
			<th>Title</th>
			<th>Author</th>
		  </tr>
_HTML;

 
 	if ($result->num_rows >0)
			{
			echo "The books list:<br><br>";
			while($row = $result->fetch_assoc()) 
				{
						echo "<tr>";
						echo "<td>".$row["field name"]."</td>";
						echo "<td>".$row["field name"]."</td>";
						echo "<td>".$row["field name"]."</td>";
						echo "</tr>";
				}
			} 
			else 
			{
				echo "0 results";
			}


print<<<_HTML
 </table>
_HTML;
				
$result->close();
$conn->close(); 
?>
 
</body>	
</html>


