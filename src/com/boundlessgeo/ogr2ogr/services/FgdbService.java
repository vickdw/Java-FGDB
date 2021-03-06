package com.boundlessgeo.ogr2ogr.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("/fgdb/{fgdbname}")
public class FgdbService {
	
	
	@GET
	public String getFgdb(@PathParam("fgdbname") String fgdbName){
		return fgdbName + " - it's alive";
	}
	
	@POST
	@Produces("application/zip")
	public Response getFgdb(@PathParam("fgdbname") String fgdbName, String geoJson){	
		try {
		String fgdbDirectory = "/opt/fgdb/";
		Date now = new Date();
		String fName = fgdbName + "-" + now.getTime();
		
		// Create a zip file name, named for the name of the geodatabase to be created.
		String outFileName =  fgdbDirectory + fName + ".zip";
		File tempFile=File.createTempFile("fgdb",".tmp");
		
		// ogr2ogr command string, these are the command line arguments you would use normally
		String[] cmd = {"-f", "FileGDB", fgdbDirectory + fName+".gdb", geoJson};
		
		//Time to zip the created file geodatabase and respond with a link to the zipped file.
			
				
		//Execute the ogr2ogr command
			ogr2ogr.main(cmd);
		
			//Create the ZipOutputStream using a new FileOutputStream named for the zip to be created.
			ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(outFileName));
			
			//Create File object from the source FileGDB
			File fgdbDir = new File(fgdbDirectory + fName+".gdb");
			
			addDirectory(zout, fgdbDir);
			
			zout.close();
			

			File outFile = new File(outFileName);
			ResponseBuilder responseBuilder = Response.ok((Object) outFile);
			responseBuilder.header("Content-Disposition", "attachment; filename=\"fgdb.zip\"");
			
			Response response= responseBuilder.build();
			
			// any cleanup necessary?
			return response;
			
			// TODO: This should report some type of error to the user and return an HTTP error code, possibly 418.
		} catch (IOException | ogr2ogrException e) {
			e.printStackTrace();
			ResponseBuilder responseBuilder=Response.serverError();
			return responseBuilder.build();
		}
		
	}
	
	private static void addDirectory(ZipOutputStream zout, File fgdbDir){
		 //get sub-folder/files list
        File[] files = fgdbDir.listFiles();
       
        System.out.println("Adding directory " + fgdbDir.getName());
       
        for(int i=0; i < files.length; i++)
        {
                //if the file is directory, call the function recursively
                if(files[i].isDirectory())
                {
                        addDirectory(zout, files[i]);
                        continue;
                }
               
                /*
                 * we are here means, its file and not directory, so
                 * add it to the zip file
                 */
               
                try
                {
                        System.out.println("Adding file " + files[i].getName());
                       
                        //create byte buffer
                        byte[] buffer = new byte[1024];
                       
                        //create object of FileInputStream
                        FileInputStream fin = new FileInputStream(files[i]);
                       
                        zout.putNextEntry(new ZipEntry(files[i].getName()));
                 
                        /*
                         * After creating entry in the zip file, actually
                         * write the file.
                         */
                        int length;
                 
                        while((length = fin.read(buffer)) > 0)
                        {
                           zout.write(buffer, 0, length);
                        }
                 
                        /*
                         * After writing the file to ZipOutputStream, use
                         *
                         * void closeEntry() method of ZipOutputStream class to
                         * close the current entry and position the stream to
                         * write the next entry.
                         */
                 
                         zout.closeEntry();
                 
                         //close the InputStream
                         fin.close();
               
                }
                catch(IOException ioe)
                {
                        System.out.println("IOException :" + ioe);                             
                }
        }
       
	}
}


