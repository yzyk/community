package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final String REPLACEMENT = "***";

    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                ) {
            String keyword;
            while ((keyword = reader.readLine()) != null) {
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("Read sensitive-words file fails: " + e.getMessage());
        }
    }

    private void addKeyword(String keyword) {
        TrieNode tmpNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            Character c = keyword.charAt(i);
            TrieNode subNode = tmpNode.getSubNodes(c);
            if (subNode == null) {
                subNode = new TrieNode();
                tmpNode.addSubNodes(c, subNode);
            }
            tmpNode = subNode;
            if (i == keyword.length() - 1) {
                tmpNode.setKeywordEnd(true);
            }
        }
    }

    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }

        TrieNode tmpNode = rootNode;
        int begin = 0;
        int position = 0;
        StringBuilder sb = new StringBuilder();

        while (position < text.length()) {
            Character c = text.charAt(position);
            if (isSymbol(c)) {
                if (tmpNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                position++;
                continue;
            }

            tmpNode = tmpNode.getSubNodes(c);
            if (tmpNode == null) {
                // [begin, position] is normal
                sb.append(text.charAt(begin));
                tmpNode = rootNode;
                position = ++begin;
            } else if (tmpNode.isKeywordEnd()) {
                // [begin position] detected as sensitive
                tmpNode = rootNode;
                begin = ++position;
                sb.append(REPLACEMENT);
            } else {
                position++;
            }
        }

        sb.append(text.substring(begin));
        return sb.toString();
    }

    private boolean isSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c);
    }

    public class TrieNode {

        private boolean isKeywordEnd = false;

        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public TrieNode getSubNodes(Character c) {
            return subNodes.get(c);
        }

        public void addSubNodes(Character c, TrieNode node) {
            subNodes.put(c, node);
        }
    }

}
