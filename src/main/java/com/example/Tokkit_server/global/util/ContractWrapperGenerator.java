package com.example.Tokkit_server.global.util;

import org.web3j.codegen.SolidityFunctionWrapperGenerator;
import org.web3j.codegen.Console;

public class ContractWrapperGenerator {
    public static void main(String[] args) throws Exception {
        String binFile = "src/main/resources/contracts/TokkitToken.bin";
        String abiFile = "src/main/resources/contracts/TokkitToken.json";
        String outputDir = "src/main/java";
        String packageName = "com.example.contract";

        String[] arguments = {
                "--binFile=" + binFile,
                "--abiFile=" + abiFile,
                "--outputDir=" + outputDir,
                "--package=" + packageName
        };

        SolidityFunctionWrapperGenerator.main(arguments);
        Console.exitSuccess(); // 종료
    }
}
