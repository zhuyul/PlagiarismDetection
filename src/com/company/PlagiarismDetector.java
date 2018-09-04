package com.company;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public interface PlagiarismDetector {
    public boolean match(List<String> tuple1, List<String> tuple2, HashMap<String, HashSet<String>> syns);
}
