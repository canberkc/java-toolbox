package com.canberkc.tools.xml.parser.stax.standard;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class StaxParser {

  public static String XML_PATH = "";

  public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
    StaxParser staxParser = new StaxParser();
    staxParser.parse();
  }

  public void parse() throws FileNotFoundException, XMLStreamException {
    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    XMLEventReader eventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(XML_PATH));

    while (eventReader.hasNext()) {
      XMLEvent nextEvent = eventReader.nextEvent();

      if (nextEvent.isStartElement()) {
        StartElement startElement = nextEvent.asStartElement();
        System.out.printf("Start: %s%n", startElement.getName().getLocalPart());

        if (startElement.getName().getLocalPart().equals("desired")) {
          String url = startElement.getAttributeByName(new QName("url")).getValue();
          String name = nextEvent.asCharacters().getData();
        }
      }

      if (nextEvent.isEndElement()) {
        EndElement endElement = nextEvent.asEndElement();
        System.out.printf("End: %s%n", endElement.getName().getLocalPart());
      }

    }

  }
}
