package com.canberkc.tools.xml;

import java.util.ArrayList;
import java.util.List;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class CustomErrorHandler implements ErrorHandler {
  final List<SAXParseException> exceptions = new ArrayList<>();

  @Override
  public void warning(SAXParseException exception) throws SAXException {
    exceptions.add(exception);
  }

  @Override
  public void error(SAXParseException exception) throws SAXException {
    exceptions.add(exception);
  }

  @Override
  public void fatalError(SAXParseException exception) throws SAXException {
    exceptions.add(exception);
  }

  public List<SAXParseException> getExceptions() {
    return exceptions;
  }

}
