/*
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the RRD-AntLR4                                                #
 * # Copyright (c) 2016-17, Philipp Kraus (philipp.kraus@flashpixx.de)                  #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU Lesser General Public License as                     #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU Lesser General Public License for more details.                                #
 * #                                                                                    #
 * # You should have received a copy of the GNU Lesser General Public License           #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */


package de.flashpixx.rrd_antlr4;

import de.flashpixx.rrd_antlr4.engine.template.ITemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.stream.Stream;


/**
 * class for any helper calls
 */
public final class CCommon
{
    /**
     * package name
     **/
    public static final String PACKAGEROOT = "de.flashpixx.rrd_antlr4";
    /**
     * properties of the package
     */
    private static final ResourceBundle PROPERTIES = ResourceBundle.getBundle(
            MessageFormat.format( "{0}.{1}", PACKAGEROOT, "configuration" ),
            Locale.getDefault(),
            new CUTF8Control()
    );
    /**
     * language resource bundle
     **/
    private static ResourceBundle s_language = ResourceBundle.getBundle(
            MessageFormat.format( "{0}.{1}", PACKAGEROOT, "language" ),
            Locale.getDefault(),
            new CUTF8Control()
    );

    /**
     * ctor - avoid instantiation
     */
    private CCommon()
    {
    }

    /**
     * sets the language
     * @param p_language language
     */
    public static void language( final Locale p_language )
    {
        Locale.setDefault( p_language );
        s_language = ResourceBundle.getBundle(
                MessageFormat.format( "{0}.{1}", PACKAGEROOT, "language" ),
                p_language,
                new CUTF8Control()
        );
    }

    /**
     * list of usable languages
     *
     * @return list of language pattern
     */
    public static String[] languages()
    {
        return Arrays.stream( PROPERTIES.getString( "translation" ).split( "," ) ).map( i -> i.trim().toLowerCase() ).toArray( String[]::new );
    }

    /**
     * returns the language bundle
     *
     * @return bundle
     */
    static ResourceBundle languagebundle()
    {
        return s_language;
    }

    /**
     * returns the property data of the package
     *
     * @return property object
     */
    static ResourceBundle configuration()
    {
        return PROPERTIES;
    }

    /**
     * concats an URL with a path
     *
     * @param p_base base URL
     * @param p_string additional path
     * @return new URL
     *
     * @throws URISyntaxException thrown on syntax error
     * @throws MalformedURLException thrown on malformat
     */
    static URL concaturl( final URL p_base, final String p_string ) throws MalformedURLException, URISyntaxException
    {
        return new URL( p_base.toString() + p_string ).toURI().normalize().toURL();
    }

    /**
     * returns root path of the resource
     *
     * @return URL of file or null
     */
    static URL resourceurl()
    {
        return CCommon.class.getClassLoader().getResource( "" );
    }

    /**
     * returns a file from a resource e.g. Jar file
     *
     * @param p_file file
     * @return URL of file or null
     *
     * @throws URISyntaxException thrown on syntax error
     * @throws MalformedURLException thrown on malformat
     */
    public static URL resourceurl( final String p_file ) throws URISyntaxException, MalformedURLException
    {
        return resourceurl( new File( p_file ) );
    }

    /**
     * returns a file from a resource e.g. Jar file
     *
     * @param p_file file relative to the CMain
     * @return URL of file or null
     *
     * @throws URISyntaxException is thrown on URI errors
     * @throws MalformedURLException is thrown on malformat
     */
    public static URL resourceurl( final File p_file ) throws URISyntaxException, MalformedURLException
    {
        if ( p_file.exists() )
            return p_file.toURI().normalize().toURL();

        final URL l_url = CCommon.class.getClassLoader().getResource( p_file.toString().replace( File.separator, "/" ) );
        if ( l_url == null )
            throw new IllegalArgumentException( CCommon.languagestring( CCommon.class, "filenotfound", p_file ) );

        return l_url.toURI().normalize().toURL();
    }

    /**
     * returns the language depend string on any object
     *
     * @param p_source any object
     * @param p_label label name
     * @param p_parameter parameter
     * @return translated string
     *
     * @tparam T object type
     */
    public static <T> String languagestring( final T p_source, final String p_label, final Object... p_parameter )
    {
        return languagestring( p_source.getClass(), p_label, p_parameter );
    }

    /**
     * returns a string of the resource file
     *
     * @param p_class class for static calls
     * @param p_label label name of the object
     * @param p_parameter object array with substitutions
     * @return resource string
     */
    public static String languagestring( final Class<?> p_class, final String p_label, final Object... p_parameter )
    {
        try
        {
            return MessageFormat.format( s_language.getString( getLanguageLabel( p_class, p_label ) ), p_parameter );
        }
        catch ( final MissingResourceException l_exception )
        {
            return "";
        }
    }

    /**
     * returns the label of a class and string to get access to the resource
     *
     * @param p_class class for static calls
     * @param p_label label name of the object
     * @return label name
     */
    private static String getLanguageLabel( final Class<?> p_class, final String p_label )
    {
        return ( p_class.getCanonicalName().toLowerCase() + "." + p_label.toLowerCase() ).replaceAll( "[^a-zA-Z0-9_\\.]+", "" ).replace(
                PACKAGEROOT + ".", "" );
    }

    /**
     * generates the full output directory
     *
     * @param p_baseoutputdirectory base output directory
     * @param p_template exporting template
     * @param p_outputdirectory relative output directory
     * @param p_extension optional path extension
     * @return full path
     */
    public static Path outputdirectory( final File p_baseoutputdirectory, final ITemplate p_template, final File p_outputdirectory, final String... p_extension )
    {
        return Paths.get( p_baseoutputdirectory.toString(),
                          Stream.concat(
                              Stream.of(
                                p_template.name(),
                                p_outputdirectory.toString()
                              ),

                              ( p_extension == null ) || ( p_extension.length == 0 )
                              ? Stream.of()
                              : Arrays.stream( p_extension )
                          ).toArray( String[]::new )
        );
    }


    /**
     * class to read UTF-8 encoded property file
     *
     * @note Java default encoding for property files is ISO-Latin-1
     */
    private static final class CUTF8Control extends ResourceBundle.Control
    {

        public final ResourceBundle newBundle( final String p_basename, final Locale p_locale, final String p_format, final ClassLoader p_loader,
                                               final boolean p_reload
        ) throws IllegalAccessException, InstantiationException, IOException
        {
            InputStream l_stream = null;
            final String l_resource = this.toResourceName( this.toBundleName( p_basename, p_locale ), "properties" );

            if ( !p_reload )
                l_stream = p_loader.getResourceAsStream( l_resource );
            else
            {

                final URL l_url = p_loader.getResource( l_resource );
                if ( l_url == null )
                    return null;

                final URLConnection l_connection = l_url.openConnection();
                if ( l_connection == null )
                    return null;

                l_connection.setUseCaches( false );
                l_stream = l_connection.getInputStream();
            }

            try
            {
                return new PropertyResourceBundle( new InputStreamReader( l_stream, "UTF-8" ) );

            }
            catch ( final Exception l_exception )
            {
                return null;
            }
            finally
            {
                l_stream.close();
            }
        }
    }

}
