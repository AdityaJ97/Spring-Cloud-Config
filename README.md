# Spring-Cloud-Config Server for Dyanamic Configuration 
## Main Advantage being server restart not required even if config files are changed.
### 1) ```spring-cloud-config-server-workspace-eclipse``` is the main file containing the server configurations.
    * It can either be imported to eclipse workspace or jar file can be created using maven.
    * Server can be started using the jar file created or directly from eclipse.
    * The server is customized to pick from composite repositries(File System and Database(Db2)), with File System as First priority.
    * The File Structure required is shown in FILE_SYSTEM_FOLDER_STRUCTURE with order {label}/{application}/{profile}
    * The database details are given in DB.
    * {label} is TenantId , {application} is Domain, {profile} is SubDomain in this case.
    * Changes should be made in .properties to configure the server.
### 2) ```API``` 
    * Contains the main Java API that makes HTTP requests to server, parses the JSON and returns the Value for given Key.
    * The Flow is :
      - First Key is Checked in Cache
      - If not found, Then http request to search in file.
      - If not found, Then http request to search in database
    * JCS cache server needs to be set-up for this
### 3) ```AngularUI_API_DEMO``` 
    * contains an Angular UI to test the Values Being fetched from File and Database.
### 4) ```File_Parser``` 
    * contains a java code to convert the File Sytem .properties into a .csv. The csv can then be imported into the database.
### 5) ```InsertIntoDbGUI``` 
    * contains a GUI in java Swing to Insert/Update values in Database.
