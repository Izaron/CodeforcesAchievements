package com.izaron.cf.helper;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// See the full list of languages in resources/analytics/languages.txt
@Log4j2
public enum Language {
    CPP("C++", "MS C++", "GNU C++", "GNU C++0x", "GNU C++11", "GNU C++14",
            "GNU C++17 Diagnostics", "Clang++17 Diagnostics", "GNU C++17", "MS C++ 2017"),
    JAVA("Java", "Java 6", "Java 7", "Java 8"),
    PASCAL("Pascal", "FPC", "Delphi", "PascalABC.NET"),
    PYTHON("Python", "Python 2", "Python 3", "PyPy 2", "PyPy 3"),
    CSHARP("C#", "Mono C#", "MS C#"),
    C("C", "GNU C", "GNU C11"),
    RUBY,
    PHP,
    HASKELL,
    FSHARP("F#", "F#"),
    SCALA,
    OCAML,
    PERL,
    COBOL,
    D,
    GO,
    JAVASCRIPT("JavaScript", "JavaScript", "Node.js"),
    KOTLIN,
    RUST,
    APRIL_FOOLS_LANGUAGE("April Fools Language", "Tcl", "Io", "Pike", "Befunge", "Factor",
            "Secret_171", "Roco", "Ada", "Mysterious Language", "FALSE", "Picat", "J", "Q#"),
    UNKNOWN("Unknown");

    private Set<String> nameSet;
    private String aliasName;

    private Language() {
        String lang = StringUtils.lowerCase(name());
        lang = StringUtils.capitalize(lang);

        this.aliasName = lang;
        this.nameSet = new HashSet<>(Collections.singletonList(lang));
    }

    private Language(String aliasName, String... names) {
        this.aliasName = aliasName;
        this.nameSet = new HashSet<>(Arrays.asList(names));
    }

    public static Language detect(String name) {
        for (Language language : Language.values()) {
            if (language.hasName(name)) {
                return language;
            }
        }
        log.debug("Tried to detect an unknown language: 'name'");
        return UNKNOWN;
    }

    public String getAliasName() {
        return aliasName;
    }

    private boolean hasName(String name) {
        return nameSet.contains(name);
    }
}
