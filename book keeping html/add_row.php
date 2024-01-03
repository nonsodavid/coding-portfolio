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
        isset($_POST['author']) &&
		isset($_POST['genre']) &&
		isset($_POST['isbn']) 
		)
		
      
  {
    $id     = assign_data($conn, 'id');
    $title  = assign_data($conn, 'title');
    $author = assign_data($conn, 'author');
	$isbn = assign_data($conn, 'isbn');
	$genre = assign_data($conn, 'genre');

    // No validation is performed so far
	$query    = "INSERT INTO book_detailing VALUES ('$id', '$title', '$author','$isbn','$genre')";
		
    $result   = $conn->query($query);
    if (!$result) echo "<br><br>INSERT failed: $query<br>" .
	
      $conn->error . "<br><br>";
  }

 else {
	  print "<br><br> You did not fill all required <br><br>";
 }



print<<<_HTML
   <form action=" " method="post">
  
     id:     <input type="text" name="id" value = ""> <br><br>
     title:  <input type="text" name="title" value = ""> <br><br>
    author : <input type="text" name="author" value = ""> <br><br>
	isbn: <input type="text" name="isbn" value = ""> <br><br>
	genre: <input type="text" name="genre" value = ""> <br><br>
    <input type="submit" value="add record">
	
   </form>
_HTML;
  
  
  function assign_data($conn, $var)
  {
    return $conn->real_escape_string($_POST[$var]);
  }
  
  
  $query  = "SELECT * FROM book_detailing";
  $result = $conn->query($query);
  if (!$result) die ("Database access failed: " . $conn->error);

  $rows = $result->num_rows;


print<<<_HTML
   <b>Here is your Books list</b>
	
	<table>
		  <tr>
			<th>id</th>
			<th>title</th>
			<th>author</th>
			<th>isbn</th>
			<th>genre</th>
		  </tr>
_HTML;

 
 	if ($result->num_rows >0)
			{
			echo "The books list:<br><br>";
			while($row = $result->fetch_assoc()) 
				{
						echo "<tr>";
						echo "<td>".$row['id']."</td>";
						echo "<td>".$row["title"]."</td>";
						echo "<td>".$row["author"]."</td>";
						echo "<td>".$row["isbn"]."</td>";
						echo "<td>".$row["genre"]."</td>";
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


