package system;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class IndexBuilder 
{
    private IndexWriter writer;

    /**
     * Create a IndexBuilder object
     * @param indexPath The path where the index files will be stored
     * @param stopWordsPath The path to the file that contains the stop words
     * @throws IOException
     */
    public IndexBuilder(String indexPath, String stopWordsPath) throws IOException
    {
        Directory directory = FSDirectory.open(Paths.get(indexPath));
        StandardAnalyzer analyzer = new StandardAnalyzer(new FileReader(stopWordsPath));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(directory, config);
    }


    /**
     * Create a Lucene's Document from java io.File
     * @param file The java io.File that will be converted
     * @return Object of type Document
     * @throws IOException
     */
    private Document toDocument(File file) throws IOException
    {
        Document doc = new Document();
        Field body = new Field("body", new FileReader(file), TextField.TYPE_NOT_STORED);
        doc.add(body);
        return doc;
    }


    /**
     * Index a single file
     * @param file the file that will be indexed
     * @throws IOException
     */
    public void indexFile(File file) throws IOException
    { 
        System.out.println("Indexing " + file.getCanonicalPath());
        Document doc = toDocument(file);
        writer.addDocument(doc);
    }

    
    /**
     * Build the index
     * @param documentsDirectory Path to the directory that contains the files 
     * to be indexed.
     * @throws IOException
     */
    public void buildIndex(String documentsDirectory) throws IOException
    {
        File [] files = new File(documentsDirectory).listFiles();
        for (File file : files) {
            indexFile(file);
        }
    }


    /**
     * Closes the IndexWriter
     * @throws IOException
     */
    public void close() throws IOException
    {
        writer.close();
    }
}