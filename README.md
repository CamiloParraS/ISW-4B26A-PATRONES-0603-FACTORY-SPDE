# Sistema de Procesamiento de Documentos Empresariales

## REQUERIMIENTOS

- Utilizar para la integración el Patrón Factory Method.
- Se debe validar por país el procesamiento de documentos.
- Los tipos de documentos son: Facturas Electrónicas, Contratos Legales, Reportes Financieros, Certificados Digitales y Declaraciones Tributarias.
- Los formatos de documentos son: .pdf, .doc, .docx, .md, .csv, .txt, .xlsx.
- El sistema debe permitir procesamiento por lotes.
- El sistema debe manejar los errores en el procesamiento de los documentos.

Taller Patrones de Software

---

## SPDE — Factory

Each country extends DocumentProcessorFactory and overrides two methods:

- getAllowedDocumentTypes() — which document types are permitted
- getAllowedFormats(DocumentType) — which file formats are allowed per type

All five document types are permitted in every country. The differences are entirely in format restrictions per type, driven by local regulatory bodies.

### Colombia (CO) - DIAN

The most permissive factory. Allows .txt and .md for Financial Reports, and accepts .doc for Legal Contracts alongside .docx

### Mexico (MX) - SAT

The strictest factory overall. No .txt, .md, or .doc anywhere. Legal Contracts are limited to .pdf and .docx only, and Tax Declarations reject .csv.

### Argentina (AR) - AFIP

Unique in accepting .csv for Electronic Invoices and .docx for Digital Certificates. The most restrictive on Tax Declarations only .pdf and .docx are accepted, blocking .xlsx, .csv, and .doc

### Chile (CL) - SII

Strictest on Financial Reports no .csv or .txt allowed. Digital Certificates are .pdf only. The only country that accepts .xlsx for Electronic Invoices.
