package system;

import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import system.IndexBuilder;


@Command(name = "indexercli", description = "build an index from a set of documents")
public class IndexerCLI implements Callable<Integer>
{
    @Option(names = "-i", paramLabel = "DOCUMENTS", description = "path to folder that contains the documents to be indexed")
    String documents;

    @Option(names = "--sw", paramLabel = "STOP_WORDS", description = "path to file that contains the stop words list")
    String stopWords;

    @Option(names = "-o", paramLabel = "OUT_FOLDER", description = "path to folder that will store the index files")
    String outFolder;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new IndexerCLI()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception
    {
        IndexBuilder indexBuilder = new IndexBuilder(outFolder, stopWords);
        indexBuilder.buildIndex(documents);
        indexBuilder.close();
        return 0;
    }
}