package com.canberkc.tools.xml;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlValidator {

  public static String XSD_PATH = "";
  public static String XML_PATH = "";

  public static void main(String[] args) throws IOException, SAXException {
    XmlValidator xmlValidator = new XmlValidator();
    List<SAXParseException> exceptions = xmlValidator.validate(XML_PATH, XSD_PATH);
    if (!exceptions.isEmpty()) {
      for (SAXParseException exception : exceptions) {
        System.out.printf("Column: %d Line: %d Reason: %s%n", exception.getColumnNumber(), exception.getLineNumber(),
            exception.getMessage());
      }
    }
  }

  public List<SAXParseException> validate(String xmlFilePath, String xsdFilePath) throws IOException, SAXException {
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    try {
      Schema schema = schemaFactory.newSchema(new File(xsdFilePath));
      Validator validator = schema.newValidator();
      CustomErrorHandler customErrorHandler = new CustomErrorHandler();
      validator.setErrorHandler(customErrorHandler);
      validator.validate(new StreamSource(new File(xmlFilePath)));
      return customErrorHandler.getExceptions();
    } catch (SAXException | IOException e) {
      e.printStackTrace();
      throw e;
    }
  }

}
