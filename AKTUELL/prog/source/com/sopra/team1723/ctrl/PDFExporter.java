package com.sopra.team1723.ctrl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

import com.sopra.team1723.data.Karteikarte;
import com.sopra.team1723.data.Karteikarte.BeziehungsTyp;
import com.sopra.team1723.data.Notiz;
import com.sopra.team1723.data.Tripel;
import com.sopra.team1723.data.Tupel;
import com.sopra.team1723.data.Veranstaltung;

public class PDFExporter
{
    private String         servletContextPath;
    private String         fileName;
    private String         texFileName;
    private String         workingDir            = "";
    private String         subFolder             = "";
    private String         dataStr               = "";
    private boolean        dataStrOpened         = false;
    private boolean        texFileCreated        = false;
    private boolean        cleaned               = false;

    private ProcessBuilder pb;
    private PDFExportThreadHandler peth;
    private Veranstaltung vn;
    private boolean exportNotizen = false;
    private boolean exportAttribute = false;
    private boolean exportVerweise = false;
    
    private static String  newLineWithSeparation = "\n";
    
    private static Map<Character, String> charReplaceList = new HashMap<>();
    private static Map<String, Tupel<String, String>> tagReplaceList = new HashMap<>();
    static{
        // Zeichen wird ersetzt durch....
        charReplaceList.put('\"', "\\grqq ");
        charReplaceList.put('�', "\"a");
        charReplaceList.put('�', "\"o");
        charReplaceList.put('�', "\"u");
        charReplaceList.put('�', "\"A");
        charReplaceList.put('�', "\"O");
        charReplaceList.put('�', "\"U");
        charReplaceList.put('�', "\"s");
        charReplaceList.put('#', "\\#");
        charReplaceList.put('%', "\\%");
        charReplaceList.put('~', "\\~");
//        charReplaceList.put("�", "\\texteuro"));        // Extra package
        charReplaceList.put('{', "\\{");
        charReplaceList.put('}', "\\}");
        charReplaceList.put('[', "\\[");
        charReplaceList.put(']', "\\]");
        charReplaceList.put('$', "\\$");
        charReplaceList.put('�', "\\�");
        charReplaceList.put('<', "\\textless ");
        charReplaceList.put('>', "\\textgreater ");
        charReplaceList.put('_', "\\_");
        charReplaceList.put('\\', "\\textbackslash ");  // Konflikt mit anderen Regeln

        
        // HTML Tag wird ersetzt durch Start und Ende Tag in Latex
        tagReplaceList.put("br", new Tupel<String, String>(
                "\\newline"+ newLineWithSeparation,
                ""));
        tagReplaceList.put("span", new Tupel<String, String>(
                "",
                ""));
        tagReplaceList.put("strong", new Tupel<String, String>(
                "\\textbf{",
                "}"));
        tagReplaceList.put("em", new Tupel<String, String>(
                "\\textit{",
                "}"));
        tagReplaceList.put("u", new Tupel<String, String>(
                "\\textsuperscript{",
                "}"));
        tagReplaceList.put("sub", new Tupel<String, String>(
                "\\textsubscript{",
                "\\textsubscript{"));
        tagReplaceList.put("ol", new Tupel<String, String>(
                "\\begin{enumerate}"+ newLineWithSeparation,
                "\\end{enumerate}"+ newLineWithSeparation));
        tagReplaceList.put("ul", new Tupel<String, String>(
                "\\begin{itemize}"+ newLineWithSeparation,
                "\\end{itemize}"+ newLineWithSeparation));
        tagReplaceList.put("li", new Tupel<String, String>(
                "\\item",
                ""+ newLineWithSeparation));
        tagReplaceList.put("h1", new Tupel<String, String>(
                "\\textbf{",
                "}"+ newLineWithSeparation));
        tagReplaceList.put("h2", new Tupel<String, String>(
                "\\textbf{",
                "}"+ newLineWithSeparation));
        tagReplaceList.put("h3", new Tupel<String, String>(
                "\\textbf{",
                "}"+ newLineWithSeparation));
        tagReplaceList.put("h4", new Tupel<String, String>(
                "\\textbf{",
                "}"+ newLineWithSeparation));
        tagReplaceList.put("h5", new Tupel<String, String>(
                "\\textbf{",
                "}"+ newLineWithSeparation));
        
        
        tagReplaceList.put("hr", new Tupel<String, String>(
                "\\hrule" + newLineWithSeparation,
                ""));
        
        // Tabellen Mapping muss abgefangen werden
        tagReplaceList.put("table", new Tupel<String, String>(
                "\\begin{tabular}","\\end{tabular}"+ newLineWithSeparation));
        tagReplaceList.put("td", new Tupel<String, String>(
                "",""));
        tagReplaceList.put("tr", new Tupel<String, String>(
                "","\\\\"+ newLineWithSeparation + "\\hline" + newLineWithSeparation));
    }
    
    
    public PDFExporter(String workingDir, String servletContextPath, Veranstaltung vn, 
            boolean exportNotizen, boolean exportAttribute, boolean exportVerweise)
    {
        this.exportNotizen = exportNotizen;
        this.exportAttribute = exportAttribute;
        this.exportVerweise = exportVerweise;
        
        this.workingDir = workingDir + "/";
        this.vn = vn;
        this.servletContextPath = servletContextPath;

        if (!new File(workingDir).exists())
        {
            System.out.println("Erstelle working dir: " + workingDir);
            if (!(new File(workingDir)).mkdirs())
                System.out.println("Kann working dir " + workingDir + " nicht erstellen!");
        }

        subFolder = String.valueOf(System.currentTimeMillis());
        texFileName = subFolder + ".tex";
        fileName = subFolder + ".pdf";
        if (!(new File(this.workingDir + subFolder)).mkdirs())
            System.out.println("Kann subfolder " + subFolder + " nicht erstellen!");
        
        dataStr = createHeader();
        dataStr += createVnInhalt();
    }

//    public void setInfo(String Author, String Titel)
//    {
//        this.Titel = Titel;
//        this.Author = Author;
//    }

    public String createHeader()
    {
        if (dataStrOpened || cleaned)
            return "";

        dataStrOpened = true;
        String header = "\\documentclass[12pt]{scrreprt}"
                + newLineWithSeparation
                + newLineWithSeparation
                + "\\usepackage[ngerman]{babel}"
                + newLineWithSeparation
                + "\\usepackage{marvosym}        % Packet f�r komplexe Tabellen"
                + newLineWithSeparation
                + "\\usepackage{array}"
                + newLineWithSeparation
                + "% Zwei Zeile f�r den ganzen Text in Arial/Helvetica"
                + newLineWithSeparation
                + "%\\usepackage[scaled]{helvet}"
                + newLineWithSeparation
                + "%\\renewcommand{\\familydefault}{\\sfdefault}   "
                + newLineWithSeparation
                + "% Paket f�r Grafiken und Abbildungen"
                + newLineWithSeparation
                + "\\usepackage{graphicx}"
                + newLineWithSeparation
                + ""
                + newLineWithSeparation
                + "% Numerierung Tabellen und Bilder kapitelunabh�ngig"
                + newLineWithSeparation
                + "\\usepackage{chngcntr}"
                + newLineWithSeparation
                + "\\counterwithout{figure}{chapter}"
                + newLineWithSeparation
                + "\\counterwithout{table}{chapter}"
                + newLineWithSeparation
                + "\\counterwithout{footnote}{chapter}"
                + newLineWithSeparation
                + ""
                + newLineWithSeparation
                + "% Packet f�r Mathe-Formeln"
                + newLineWithSeparation
                + "\\usepackage{amsmath, amssymb}"
                + newLineWithSeparation
                + "\\usepackage{float}"
                + newLineWithSeparation
                + ""
                + newLineWithSeparation
                + "% F�r besondere Abs�tze"
                + newLineWithSeparation
                + "\\parindent 0pt      % Einr�ckung bei Absatz"
                + newLineWithSeparation
                + "\\parskip 6pt        % Abst�nde zwischen Abs�tzen"
                + newLineWithSeparation
                + ""
                + newLineWithSeparation
                + "% Inhalte f�r den Titel des Dokuments"
                + newLineWithSeparation
                + "\\subject{eLearning-PDF-Export}"
                + newLineWithSeparation
                + "\\title{"
                + vn.getTitel()
                + "}"
                + newLineWithSeparation
                + "\\author{"
                + vn.getErsteller().getVorname() + " " + vn.getErsteller().getNachname() 
                + "}"
                + newLineWithSeparation
                + "% Hart rein coden oder \\today"
                + newLineWithSeparation
                + "\\date{\\today}"
                + newLineWithSeparation
                + ""
                + newLineWithSeparation
                + "% Inhaltsverzeichnis klickbar machen, allgemein Verweise als Links."
                + newLineWithSeparation
                + "% Au�erdem wird ein Inhaltsverzeichnis im AdobeReader erzeugt. Also klickbares Inhaltsverzeichnis an der Seite."
                + newLineWithSeparation + "\\usepackage{hyperref}" + newLineWithSeparation + "" + newLineWithSeparation
                + "\\begin{document}" + newLineWithSeparation + "% Titelseite erstellen" + newLineWithSeparation
                + "\\maketitle" + newLineWithSeparation + "" + newLineWithSeparation
                + "% Hier erscheint das Inhaltsverzeichnis (Nach dem hinzuf�gen 2 mal kompilieren)"
                + newLineWithSeparation + "\\tableofcontents" + newLineWithSeparation;

        return header;
    }
    private String createVnInhalt()
    {

        if (!dataStrOpened || cleaned)
            return "";
        
        String data = "\\chapter{Veranstaltungsbeschreibung}" + newLineWithSeparation 
                + transformHTMLToLaTeX(vn.getBeschreibung())+ newLineWithSeparation
                + "\\newpage";

        return data;
    }
    private boolean createAndCloseFile()
    {
        if (!dataStrOpened || cleaned)
            return false;

        dataStr += "\\listoffigures" + newLineWithSeparation + "\\end{document}" + newLineWithSeparation;
        dataStrOpened = false;

        FileWriter writer = null;
        try
        {

            writer = new FileWriter(workingDir + subFolder + "/" + texFileName, false);
            writer.write(dataStr, 0, dataStr.length());
            writer.close();
            texFileCreated = true;
            System.out.println("String in File " + subFolder + "/" + texFileName + " geschrieben.");
        }
        catch (IOException ex)
        {
            System.out.println("Fehler beim schreiben der TexDatei.");
            ex.printStackTrace();
        }
        return true;
    }

    private boolean startConvertFile()
    {
        if (!texFileCreated || cleaned || peth != null)
            return false;

        pb = new ProcessBuilder("pdflatex", "-synctex=1", "-interaction=nonstopmode", workingDir + subFolder + "/"
                + texFileName);
        pb.directory(new File(workingDir + subFolder));
        pb.redirectErrorStream(true);
        
        peth = new PDFExportThreadHandler(this, pb);
        new Thread(peth).start();
        
        return true;
    }

    public boolean appendKarteikarte(Karteikarte kk, int depth, Notiz n)
    {
        if (texFileCreated || cleaned || !dataStrOpened || depth < 0)
            return false;

        System.out.println("Append KK: " + kk.getTitel());
        switch (kk.getTyp())
        {
            case VIDEO:
                System.out.println("Video KK - nicht unterst�tzt");
                dataStr += "Video " + replaceInvalidChars(kk.getTitel() + " (�bersprungen)") + newLineWithSeparation;
                break;
            case BILD:
                String Strkk = createBildKKLatexStr(kk, depth);
                dataStr += Strkk + newLineWithSeparation;
                break;
            case TEXT:
                String Strkk1 = createTextKKLatexStr(kk, depth);
                dataStr += Strkk1 + newLineWithSeparation;
                break;

            default:
                return false;
        }
        
        dataStr+=exportVerweise(kk) + newLineWithSeparation;
        
        if(exportNotizen && n != null && !n.getInhalt().equals(""))
        {
            dataStr += "\\paragraph{Notizen}" + newLineWithSeparation
                    +  transformHTMLToLaTeX(n.getInhalt())  + newLineWithSeparation;
        }
                
        return true;
    }
    
    private String exportVerweise(Karteikarte kk)
    {
        if(!exportVerweise ||  kk.getVerweise().size() == 0)
            return "";
        
        String dataStr = "\\paragraph{Querverweise}" + newLineWithSeparation
                + "\\begin{description}" + newLineWithSeparation;

        ArrayList<Tupel<Integer, String>> vorraussetzung = new ArrayList<>();
        ArrayList<Tupel<Integer, String>> zusatz = new ArrayList<>();
        ArrayList<Tupel<Integer, String>> sonstiges = new ArrayList<>();
        ArrayList<Tupel<Integer, String>> uebung = new ArrayList<>();
        
        for( Tripel<BeziehungsTyp, Integer, String> v: kk.getVerweise())
        {
            if(v.x == BeziehungsTyp.V_VORAUSSETZUNG)
                vorraussetzung.add(new Tupel<Integer, String>(v.y, v.z));
            else if(v.x == BeziehungsTyp.V_UEBUNG)
                uebung.add(new Tupel<Integer, String>(v.y, v.z));
            else if(v.x == BeziehungsTyp.V_SONSTIGES)
                sonstiges.add(new Tupel<Integer, String>(v.y, v.z));
            else if(v.x == BeziehungsTyp.V_ZUSATZINFO)
                zusatz.add(new Tupel<Integer, String>(v.y, v.z));
        }

        if (vorraussetzung.size() > 0)
        {
            dataStr += "\\item[Vorraussetzung:]" + newLineWithSeparation;

            for (int i = 0; i < vorraussetzung.size(); i++)
            {
                Tupel<Integer, String> t = vorraussetzung.get(i);
                dataStr += "\\nameref{kk_" + t.x +"}";
                if (i < vorraussetzung.size() - 1)
                    dataStr += ", ";
            }
            dataStr += newLineWithSeparation;
        }
        if (uebung.size() > 0)
        {
            dataStr += "\\item[\"Ubung:]" + newLineWithSeparation;

            for (int i = 0; i < uebung.size(); i++)
            {
                Tupel<Integer, String> t = uebung.get(i);
                dataStr += "\\nameref{kk_" + t.x +"}";
                if (i < uebung.size() - 1)
                    dataStr += ", ";
            }
            dataStr += newLineWithSeparation;
        }
        if (zusatz.size() > 0)
        {
            dataStr += "\\item[Zusatzinfo:]" + newLineWithSeparation;

            for (int i = 0; i < zusatz.size(); i++)
            {
                Tupel<Integer, String> t = zusatz.get(i);
                dataStr += "\\nameref{kk_" + t.x +"}";
                if (i < zusatz.size() - 1)
                    dataStr += ", ";
            }
            dataStr += newLineWithSeparation;
        }
        if (sonstiges.size() > 0)
        {
            dataStr += "\\item[Sonstiges:]" + newLineWithSeparation;

            for (int i = 0; i < sonstiges.size(); i++)
            {
                Tupel<Integer, String> t = sonstiges.get(i);
                dataStr += "\\nameref{kk_" + t.x +"}";
                if (i < sonstiges.size() - 1)
                    dataStr += ", ";
            }
            dataStr += newLineWithSeparation;
        }

        dataStr +=  "\\end{description}" + newLineWithSeparation;
        return dataStr;
    }
    public PDFExportThreadHandler getExecutor()
    {
        return peth;
    }

    private String createTextKKLatexStr(Karteikarte kk, int depth)
    {

        if (kk.getTitel().equals(""))
        {
            return putStrIntoChapter(depth, kk, "");
        }
        else
        {
            String data = kk.getInhalt();
            return putStrIntoChapter(depth, kk, transformHTMLToLaTeX(data));
        }
    }

    private String transformHTMLToLaTeX(String html)
    {
        Document doc = Jsoup.parse("<html><body>" + html + "</body></html>");
        String latexStr = recursiveTransformHTLMtoLatex(doc.body(), 0);
        return latexStr;
    }

    private String recursiveTransformHTLMtoLatex(Element n, int depth)
    {
        List<Element> nodes = n.children();
        String result = "";


        if (!n.nodeName().equals("body"))
        {
//            for (int i = 0; i < depth; i++)
//                System.out.print("  ");
//            System.out.println("<" + n.nodeName() + ">");
            result += mapHTMLTagToLaTeXTag(n, true) + " ";
        }
        
        
        if (nodes.size() == 0)
        {
            String content = n.text();
//            for (int i = 0; i < depth; i++)
//                System.out.print("  ");
            // System.out.println(content);
            if (n.attr("class").equals("mathjax_formel"))
            {
                System.out.println("Skip replacing. Formel gefunden");
                result += content;
            }
            else
                result += replaceInvalidChars(content);
        }
        else
        {
            for (Element n2 : nodes)
            {
                result += recursiveTransformHTLMtoLatex(n2, depth + 1);
            }
        }
        
        if (!n.nodeName().equals("body"))
        {
//            for (int i = 0; i < depth; i++)
//                System.out.print("  ");
//            System.out.println("</" + n.nodeName() + ">");
            result += mapHTMLTagToLaTeXTag(n, false);
        }
        return result;
    }
    
    private String replaceInvalidChars(String html)
    {
        String result = "";
        for(int i = 0; i < html.length(); i++)
        {
            String replacer = charReplaceList.get(html.charAt(i));
            if(replacer == null)
                result += html.charAt(i);
            else
                result += replacer;
        }
        return result;
    }

    private String mapHTMLTagToLaTeXTag(Node node, boolean begin)
    {
        Tupel<String, String> replacer = tagReplaceList.get(node.nodeName().toLowerCase());
        if(replacer==null)
            return "";
        
        if(node.nodeName().toLowerCase().equals("table"))
        {
            if(begin)
            {
                int colCnt = node.childNode(0).childNodeSize();
                String tableConfig = "{|";
                for(int i = 0; i < colCnt; i++)
                {
                    tableConfig += "c|";
                }
                tableConfig += "}";
                
                return replacer.x + tableConfig + newLineWithSeparation;
            }
            else 
                return replacer.y;  
        }
        else if(node.nodeName().toLowerCase().equals("td"))
        {
            if(begin)
            {
                int idx = node.siblingIndex();
                if(idx == 0)
                    return replacer.x;
                else
                    return " & " + replacer.x;
            }
            else 
                return replacer.y;  
        }
        else
        {
            if(begin)
                return replacer.x;
            else 
                return replacer.y;
        }
        
        
    }

    private String putStrIntoChapter(int chapterDepth, Karteikarte kk, String data)
    {
        String newData = null;
        String title = replaceInvalidChars(kk.getTitel());
        
        switch (chapterDepth)
        {
            case 0:
                newData = "\\chapter["+ title +"]{" + title + createAttribute(kk) + "}" + newLineWithSeparation;
                break;
            case 1:
                newData = "\\section["+ title +"]{" + title + createAttribute(kk) + "}" + newLineWithSeparation;
                break;
            case 2:
                newData = "\\subsection["+ title +"]{" + title + createAttribute(kk) + "}" + newLineWithSeparation;
                break;
            case 3:
                newData = "\\subsubsection["+ title +"]{" + title + createAttribute(kk) + "}" + newLineWithSeparation;
                break;
            case 4:
                newData = "\\paragraph["+ title +"]{" + title + createAttribute(kk) + "}" + newLineWithSeparation;
                break;

            default:
                newData = "";
                break;
        }
        newData += "\\label{kk_" + kk.getId() + "}";
        return newData + newLineWithSeparation + data;
    }

    private String createBildKKLatexStr(Karteikarte kk, int depth)
    {
        String bildPfad = servletContextPath + "/files/images/" + kk.getId() + ".png";
        System.out.println("BildPfad: " + bildPfad);
        System.out.println("Copy to: " + workingDir + subFolder + "/" + kk.getId() + ".png");
        try
        {
            FileUtils.copyFile(new File(bildPfad), new File(workingDir + subFolder + "/" + kk.getId() + ".png"));

            String latexStr = "\\begin{figure}[H] " + newLineWithSeparation + "  \\centering" + newLineWithSeparation
                    + "  \\includegraphics[width=0.5\\linewidth]{" + kk.getId() + ".png}" + newLineWithSeparation
                    + "  \\caption{" + replaceInvalidChars(kk.getTitel()) + createAttribute(kk) + "}" + newLineWithSeparation + "  \\label{kk_"
                    + kk.getId() + "}" + newLineWithSeparation + "\\end{figure}" + newLineWithSeparation;
            
            
            // TODO Bilder mit �berschrift ?
            return latexStr;// putStrIntoChapter(depth,kk.getTitel(), latexStr);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return "";
    }

    public String createAttribute(Karteikarte kk)
    {
        if(!exportAttribute)
            return "";
        
        String tmp = "\\protect\\footnote{Attribute: ";
        int cnt = 0;
        if(kk.isIstSatz())
            tmp += (cnt++==0?"":", ") + "Satz";
        if(kk.isIstBeweis())
            tmp += (cnt++==0?"":", ") + "Beweis";
        if(kk.isIstDefinition())
            tmp += (cnt++==0?"":", ") + "Definition";
        if(kk.isIstWichtig())
            tmp += (cnt++==0?"":", ") + "Wichtig";
        if(kk.isIstGrundlage())
            tmp += (cnt++==0?"":", ") + "Grundlage";
        if(kk.isIstZusatzinfo())
            tmp += (cnt++==0?"":", ") + "Zusatzinfo";
        if(kk.isIstExkurs())
            tmp += (cnt++==0?"":", ") + "Exkurs";
        if(kk.isIstBeispiel())
            tmp += (cnt++==0?"":", ") + "Beispiel";
        if(kk.isIstUebung())
            tmp += (cnt++==0?"":", ") + "Uebung";
        
        tmp += "}";
        if(cnt>0)
            return tmp;
        else
            return "";
    }
    public boolean startConvertToPDFFile()
    {
        if (!createAndCloseFile())
            return false;

        // 2 Mal kompilieren !
        startConvertFile();

        return true;

    }
    public String getPDFFileName()
    {
        return fileName;
    }
    public String getTexFileName()
    {
        return texFileName;
    }
    
    public void deleteFiles()
    {
        if(!peth.creationFinished())
            return;
        System.out.println("L�sche erstellte Dateien.");
        
        if(peth.creationSucessfull())
            FileUtils.deleteQuietly(new File(workingDir + fileName));
        
        FileUtils.deleteQuietly(new File(workingDir + texFileName));
    }

    /**
     * Entfernt den tempor�ren Ordner und, dass nur noch die PDF �brig ist.
     */
    public void cleanUp(boolean copyPossible)
    {
        if (!texFileCreated || cleaned)
            return;

        try
        {
            // Copy nach oben
            if(copyPossible)
                FileUtils.copyFile(new File(workingDir + subFolder + "/" + fileName), new File(workingDir + fileName));
            
            // Tex file immer sichern
            FileUtils.copyFile(new File(workingDir + subFolder + "/" + texFileName), new File(workingDir + texFileName));
            
            FileUtils.deleteDirectory(new File(workingDir + subFolder));
            cleaned = true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
