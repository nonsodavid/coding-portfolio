html>
<head>
	<meta charset = "utf-8">
	<style type="text/css">
		td, th {border:1px solid black;}
	</style>
</head>


<body>

<?php // connect.php allows connection to the database

  require_once 'connect.php'; //using require will include the connect.php file each time it is called.

    if (isset($_POST['old'])   &&
        isset($_POST['new']) &&
        isset($_POST['old'])
		)
		
      
  {
    $field     = assign_data($conn, 'field');
    $old  = assign_data($conn, 'old');
    $new = assign_data($conn, 'new');
    
    // No validation is performed so far
	$query    = "UPDATE book_detailing SET $field = '$new' WHERE $field = '$old'";
		
    $result   = $conn->query($query);
    if (!$result) echo "<br><br>INSERT failed: $query<br>" .
	
      $conn->error . "<br><br>";
  }

 else {
	  print "<br><br> You did not fill all required <br><br>";
 }



print<<<_HTML
   <form action=" " method="post">
  
    Field to be updated:     <input type="text" name="field" value = ""> <br><br>
    Old value:  <input type="text" name="old" value = ""> <br><br>
    New value: <input type="text" name="new" value = ""> <br><br>
      
    <input type="submit" value="update record">
	
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
			<th> id</th>
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


