package com.cmu.privacyplugin;

import java.util.ArrayList;

public class PrivadoOutput {

    public String privadoCoreVersion;
    public String privadoCLIVersion;
    public String privadoMainVersion;
    public String privadoLanguageEngineVersion;
    public long createdAt;
    public String repoName;
    public String language;
    public GitMetadata gitMetadata;
    public String localScanPath;
    public ArrayList<String> probableSinks;
    public ArrayList<Source> sources;
    public ArrayList<Sink> sinks;
    public ArrayList<SinkProcessing> sinkProcessing;
    public ArrayList<Collection> collections;
    public ArrayList<Processing> processing;
    public DataFlow dataFlow;

    public class Collection{
        public String collectionId;
        public String name;
        public boolean isSensitive;
        public ArrayList<Collection> collections;
        public String sourceId;
        public ArrayList<Occurrence> occurrences;
    }

    public class DatabaseDetails{
        public String dbName;
        public String dbVendor;
        public String dbLocation;
        public String dbOperation;
    }

    public class DataFlow{
        public ArrayList<Storage> storages;
        public ArrayList<Object> miscellaneous;
        public ArrayList<Object> internal_apis;
        public ArrayList<Leakage> leakages;
        public ArrayList<ThirdParty> third_parties;
    }

    public class GitMetadata{
        public String branchName;
        public String commitId;
        public String remoteUrl;
    }

    public class Leakage{
        public String sourceId;
        public ArrayList<Sink> sinks;
    }

    public class Occurrence{
        public String sample;
        public int lineNumber;
        public int columnNumber;
        public String fileName;
        public String excerpt;
        public String endPoint;
    }

    public class Path{
        public String pathId;
        public ArrayList<Path2> path;
    }

    public class Path2{
        public String sample;
        public int lineNumber;
        public int columnNumber;
        public String fileName;
        public String excerpt;
    }

    public class Processing{
        public String sourceId;
        public ArrayList<Occurrence> occurrences;
    }

    public class Sink{
        public String sourceType;
        public String sinkType;
        public String id;
        public String name;
        public ArrayList<String> domains;
        public ArrayList<String> apiUrl;
        public DatabaseDetails databaseDetails;
        public String category;
        public String sensitivity;
        public boolean isSensitive;
        public Tags tags;
        public ArrayList<Path> paths;
    }

    public class SinkProcessing{
        public String sinkId;
        public ArrayList<Occurrence> occurrences;
    }

    public class Source{
        public String sourceType;
        public String id;
        public String name;
        public String category;
        public String sensitivity;
        public boolean isSensitive;
        public Tags tags;
    }

    public class Storage{
        public String sourceId;
        public ArrayList<Sink> sinks;
    }

    public class Tags{
        public String law;
    }

    public class ThirdParty{
        public String sourceId;
        public ArrayList<Sink> sinks;
    }

}
