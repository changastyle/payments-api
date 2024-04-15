package com.vd.payments;

public class ConsoleTest
{
    public static void main(String[] args) {
        // Código ANSI para el color rojo
        String redColorCode = "\033[31m";
        String resetColorCode = "\033[0m";

        // Texto que se imprimirá en rojo
        String text = "ERROR: ";


        String msg = "Fallo el condensador de flujo";
        // Imprimir en rojo
        System.out.println(redColorCode + text + resetColorCode + msg);
    }
}
