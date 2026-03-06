package processor;

import model.CountryCode;
import model.DocumentType;
import model.FileFormat;
import model.ProcessingResult;

import java.io.File;
import java.util.List;


public interface DocumentProcessor {

  
    ProcessingResult process(File file);

  
    DocumentType getDocumentType();

   
    CountryCode getCountry();

   
    List<FileFormat> getSupportedFormats();


    String getDescription();
}
