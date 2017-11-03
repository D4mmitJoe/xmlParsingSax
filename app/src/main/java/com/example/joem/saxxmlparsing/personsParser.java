package com.example.joem.saxxmlparsing;

import android.util.Xml;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by JoeM on 10/9/2017.
 */

//handler class
public class personsParser {
    //class that will hold logic, capture events, perform parsing of xml
    //DefaultHandler enables us to capture events for xml
    public static class PersonsSAXParser extends DefaultHandler{
        ArrayList<Person> persons; //array list of persons we successfully parsed from xml
        Person person;
        Address address;
        StringBuilder innerXml; //need stringBuilder to capture strings like 'name' when they open

        //enables us to trigger parsing events and return data
        static public ArrayList<Person> parsePersons(InputStream inputStream) throws IOException, SAXException {
            PersonsSAXParser parser = new PersonsSAXParser();
            //pass in 3 parameters: 1 input stream, 2 encoding, 3 content handler (whatever implements the handler)
            //can create exception so we added exception to method itself ("throws.." above)
            //takes input stream, generate events (startDocument, startElement, etc below), passes events to parser
            Xml.parse(inputStream, Xml.Encoding.UTF_8, parser);
            return parser.persons; //persons generated after parsing
        }
        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            this.persons = new ArrayList<>();
            this.innerXml = new StringBuilder();
        }
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if (localName.equals("person")){
                //when we see opening person tag we create new person object
                person = new Person();
                //because ID is set to 'long' we need 'Long.valueOf'
                //can cause an exception that needs to be handled (not covered here)
                person.id = Long.valueOf(attributes.getValue("id"));//associate id to person from attributes
            }else if (localName.equals("address")){
                address = new Address();
            }
        }
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);

            String text = "";
            if (innerXml.toString() != null){
                text = innerXml.toString().trim();//trim removes leading space, to prevent errors (like, "age = ' 25'")
            }

            if (localName.equals("name")){//when name tag closes we associate this name to person object
                person.name = text;
            }else if (localName.equals("age")){//when age tag closes we associate this age to person object
                //need to do sanity checks bc changing int to string, which can cause an exception (safest way is try-catch block, not covered here)
                person.age = Integer.valueOf(text);
            }else if (localName.equals("line1")){//when line1 tag closes we associate this line1 to address of person object
                address.line1 = text;
            }else if (localName.equals("city")){//when city tag closes we associate this city to address of person object
                address.city = text;
            }else if (localName.equals("state")){//when state tag closes we associate this state to address of person object
                address.state = text;
            }else if (localName.equals("zip")){//when zip tag closes we associate this zip to address of person object
                address.zip = text;
            }else if (localName.equals("address")){//at address closing tag, we associate address object with person (object) address
                person.address = address;
            }else if (localName.equals("person")){//when person closes we add person to persons array list
                persons.add(person);
            }

            //after tag close we have to clear the string builder
            innerXml.setLength(0);//clears stringBuilder's buffer
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            //characters need to be appended to stringBuilder
            //after opening tag, characters are captured here
            innerXml.append(ch, start, length);
        }
    }
}
