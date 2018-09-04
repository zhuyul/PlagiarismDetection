package com.company;

import java.io.*;
import java.util.*;

public class PlagiarismDetection implements PlagiarismDetector {

    public double plagiarism(String syns, String file1, String file2, int n) throws IOException {

        HashMap<String, HashSet<String>> synonyms = synonyms(syns);
        List<List<String>> tuples1 = fileToTuples(file1, n);
        List<List<String>> tuples2 = fileToTuples(file2, n);
        double count = 0;

        //assumption here: we call two tuples a match even if their locations in the sentences are different
        //f.e. in sentences "go for a run" and "for a jog go", we consider "for a run" and "for a jog" a match
        for (List<String> tuple1 : tuples1) {
            for (List<String> tuple2 : tuples2) {
                if (match(tuple1, tuple2, synonyms)) {
                    count++;
                }
            }
        }

        //assumption here: two input files are with same length
        return count/tuples1.size();
    }

    @Override
    public boolean match(List<String> tuple1, List<String> tuple2, HashMap<String, HashSet<String>> syns) {
        for (int i = 0; i < tuple1.size(); i++) {
            String word1 = tuple1.get(i);
            String word2 = tuple2.get(i);

            //no need to compare two identical words
            if (!word1.equals(word2)) {
                if (syns.containsKey(word1) && syns.containsKey(word2)) {
                    if (!syns.get(word1).equals(syns.get(word2))) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }

        return true;
    }

    private HashMap<String, HashSet<String>> synonyms(String syns) throws IOException {
        BufferedReader br = null;
        HashMap<String, HashSet<String>> synonyms = new HashMap<>();

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(syns)));
            String line;

            while ((line = br.readLine()) != null) {
                String[] words = line.toLowerCase().split(" ");
                for (String word : words) {
                    //assumption here: same word might appear in different synonyms groups.
                    HashSet<String> set = synonyms.get(word) == null ? new HashSet<>() : synonyms.get(word);
                    set.addAll(Arrays.asList(words));
                    synonyms.put(word, set);
                }
            }
        }
        finally {
            if (br != null) {
                br.close();
            }
        }

        return synonyms;
    }

    private List<List<String>> fileToTuples(String file, int n) throws IOException {
        BufferedReader br = null;
        List<List<String>> tuples = new ArrayList<>();

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;

            while ((line = br.readLine()) != null) {
                String[] words = line.toLowerCase().split(" ");
                for (int i = 0; i <= words.length - n; i++) {
                    List<String> tuple = new ArrayList<>();
                    for (int j = i; j < i + n; j++) {
                        tuple.add(words[j]);
                    }
                    tuples.add(tuple);
                }
            }
        }
        finally {
            if (br != null) {
                br.close();
            }
        }

        return tuples;
    }

    public static void main(String[] args) throws IOException {

        if (args.length < 3 || args.length > 4) {
            System.err.println("requires 3 or 4 arguments");
            System.exit(-1);
        }

        String syns = "static/" + args[0];
        String file1 = "static/" + args[1];
        String file2 = "static/" + args[2];
        int n = args.length == 4 ? Integer.parseInt(args[3]) : 3;

        PlagiarismDetection p = new PlagiarismDetection();
        System.out.println(String.format("%.0f%%",p.plagiarism(syns, file1, file2, n) * 100));
    }
}
