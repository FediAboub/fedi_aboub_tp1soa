package org.example;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        String xmlPath = "src/main/resources/club.xml";
        String xsdPath = "src/main/resources/club.xsd";

        // --- Étape 1 : Validation ---
        if (validateXML(xmlPath, xsdPath)) {
            System.out.println("✅ XML valide par rapport au schéma !");
        } else {
            System.out.println("❌ XML invalide !");
            return;
        }

        // --- Étape 2 : Interrogation XPath ---
        try {
            // Charger le document XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(xmlPath));

            XPathFactory xpathFactory = XPathFactory.newInstance();
            XPath xpath = xpathFactory.newXPath();

            System.out.println("\n🔹 Requête 1 : noms des membres du bureau");
            XPathExpression expr1 = xpath.compile("/club/bureau/membre/nom/text()");
            var nodes1 = (org.w3c.dom.NodeList) expr1.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes1.getLength(); i++) {
                System.out.println(" - " + nodes1.item(i).getNodeValue());
            }

            System.out.println("\n🔹 Requête 2 : emails des membres simples actifs");
            XPathExpression expr2 = xpath.compile("/club/membres/membreSimple[actif='true']/email/text()");
            var nodes2 = (org.w3c.dom.NodeList) expr2.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes2.getLength(); i++) {
                System.out.println(" - " + nodes2.item(i).getNodeValue());
            }

            System.out.println("\n🔹 Requête 3 : chefs ayant plus de 2 projets");
            XPathExpression expr3 = xpath.compile("/club/cabinet/chef[nombreProjets > 2]/nom/text()");
            var nodes3 = (org.w3c.dom.NodeList) expr3.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodes3.getLength(); i++) {
                System.out.println(" - " + nodes3.item(i).getNodeValue());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode de validation XML/XSD
    public static boolean validateXML(String xmlPath, String xsdPath) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlPath)));
            return true;
        } catch (SAXException | IOException e) {
            System.out.println("Erreur : " + e.getMessage());
            return false;
        }
    }
}
