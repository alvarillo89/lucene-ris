package system;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class QueryEngine {
    private IndexSearcher searcher;
    private QueryParser parser;
    Directory directory;
    DirectoryReader reader;
    private static final int TOP_N = 10;

    /**
     * Creates a Query object:
     * 
     * @param indexPath     The path of the directory where the index files is
     *                      stored
     * @param stopWordsPath The path to the file that contains the stop words
     * @throws IOException
     */
    public QueryEngine(String indexPath, String stopWordsPath) throws IOException {
        directory = FSDirectory.open(Paths.get(indexPath));
        reader = DirectoryReader.open(directory);
        StandardAnalyzer analyzer = new StandardAnalyzer(new FileReader(stopWordsPath));
        searcher = new IndexSearcher(reader);
        parser = new QueryParser("body", analyzer);
    }

    /**
     * Get the top 10 documents that match an input query
     * @param inputQuery The input Query
     * @return ArrayList of Result objects with the information of the documents found
     * @throws ParseException
     * @throws IOException
     */
    public ArrayList<Result> Find(String inputQuery) throws ParseException, IOException {
        ArrayList<Result> out = new ArrayList<>();
        Result tmp;
        Query query = parser.parse(inputQuery);
        ScoreDoc[] score = searcher.search(query, TOP_N).scoreDocs;

        for (ScoreDoc scoreDoc : score) {
            Document doc = searcher.doc(scoreDoc.doc);
            tmp = new Result();
            tmp.setPath(doc.getField("path").stringValue());
            tmp.setContent(doc.getField("body").stringValue());
            out.add(tmp);
        }
        return out;
    }

    /**
     * Closes the IndexSearcher
     * @throws IOException
     */
    public void close() throws IOException {
        directory.close();
        reader.close();
    }
}