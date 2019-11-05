/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;

import java.util.List;
import java.util.Scanner;

/**
 *
 * @author Velan
 */

/**
 * Clase que contiene todos los métodos de interfaz que
 * permiten interactuar con el Usuario
 */

public class InterfazConsola
{

	// ----------------------------
	//  Constantes
	// ----------------------------


	/**
	 * Constante que indica el número de consecutivo de 
	 * la solicitud
	 */
	

	// ----------------------------
	//  Atributos
	// ----------------------------

	/**
	 * Permite leer los diferentes valores ingresador por teclado por el Usuario
	 */
	private Scanner sc;


	// ----------------------------
	//  Relaciones
	// ----------------------------


	// ----------------------------
	//  Constructores
	// ----------------------------

	/**
	 * Método que inicializa las variables y relaciones de la Clase Interfaz Consola
	 */
	public InterfazConsola() 
	{
            sc = new Scanner(System.in);
	}


	// ----------------------------
	//  Métodos
	// ----------------------------

	/**
	 * Método que muestra el Menú Principal
	 * @param menu - Lista con las diferentes opciones del Menú Principal
         * @param titulo - Titulo del menú
	 * @return opcionSeleccionada - Opción que el usuario selecciono
	 */
	public int mostrarMenu(List<String> menu, String titulo)
	{
		imprimirLn();
		imprimirLn( titulo.toUpperCase() );
		imprimirLn();
		String opcionActual = null;
		for (int posOpcionActual = 0; posOpcionActual < menu.size(); posOpcionActual++) 
		{
			opcionActual = menu.get(posOpcionActual);

			imprimirLn("opción " + posOpcionActual + ": " + opcionActual + '.' );
		}

		imprimirLn();

		int opcionSeleccionada = leerInt("Selecciona una opción");

		return opcionSeleccionada;
	}

	/**
	 * Método que imprime un espacio
	 */
	public void imprimirLn()
	{
		imprimirLn("");
	}

	/**
	 * Método que imprime un enunciado
	 * @param mensaje - Enunciado que se desea imprimir
	 */
	public void imprimirLn(String mensaje)
	{
		System.out.println(mensaje);
	}

	/**
	 * Método que imprime un enunciado sin \n
	 * @param mensaje - Enunciado que se desea imprimir
	 */
	public void imprimir(String mensaje)
	{
		System.out.print(mensaje + " ");
	}

	/**
	 * Métoto que imprime un error que se haya generado en el sistema
	 * @param mensaje - Descripción del error
	 */
	public void imprimirError(String mensaje) 
	{
		System.err.println( "======================================================\n=   ERROR: " + mensaje + ".\n======================================================" );
	}

	/**
	 * Método que lee un entero ingresado por teclado por el usuario
	 * @param enunciado - Texto que solicita algún dato al usuario 
	 * @return numero - Entero leido
	 */
	public int leerInt(String enunciado)
	{
		imprimirLn(enunciado + ":");
		imprimir(" > ");

		int numero = -999;

		try
		{
			numero = sc.nextInt();
		}
		catch ( Exception e )
		{
			imprimirError( "Tipo de dato invalido " + e.getMessage() );;
		}

		sc.nextLine();

		return numero;
	}

	/**
	 * Método que lee un String
	 * @param enunciado - Texto que solicita algún dato al usuario 
	 * @return texto - String leido
	 */
	public String leerString(String enunciado)
	{
		imprimirLn(enunciado + ":");
		imprimir(" > ");
		String texto = null;
		try
		{
			texto = sc.nextLine();				
		}
		catch (Exception e)
		{
			imprimirError( "Ingeso un dato inválido " + e.getMessage() );
		}
		return texto;
	}

	/**
	 * Método que da lectura a un nombre de un archivo ingresado por teclado
	 * @return texto - Nombre del archivo 
	 */
	public String leerNombreArchivo()
	{
		return leerString("Ingrese la ruta del archivo");
	}

}
