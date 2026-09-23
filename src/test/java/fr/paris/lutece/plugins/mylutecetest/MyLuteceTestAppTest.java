/*
 * Copyright (c) 2002-2026, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.mylutecetest;

import java.lang.reflect.Proxy;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.service.security.LuteceAuthentication;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * Tests the XPage showing the signed-in front-office user.
 */
public class MyLuteceTestAppTest extends LuteceTestCase
{
    private static final String AUTH_SERVICE_NAME = "E2E Test Authentication";

    /**
     * An anonymous visitor is sent to the authentication.
     */
    @Test
    public void testAnonymousVisitorIsRefused( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );

        assertThrows( UserNotSignedException.class, ( ) -> new MyLuteceTestApp( ).getPage( request, 0, null ) );
    }

    /**
     * The page of a signed-in user shows the user's data with labels in the visitor's language.
     *
     * @throws UserNotSignedException
     *             if the user is not registered
     */
    @Test
    public void testSignedInUserPageIsLocalized( ) throws UserNotSignedException
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addPreferredLocale( Locale.FRENCH );
        LuteceAuthentication authentication = (LuteceAuthentication) Proxy.newProxyInstance( getClass( ).getClassLoader( ),
                new Class<?> [ ] { LuteceAuthentication.class },
                ( proxy, method, args ) -> "getAuthServiceName".equals( method.getName( ) ) ? AUTH_SERVICE_NAME : null );
        LuteceUser user = new LuteceUser( "jdoe", authentication )
        {
            private static final long serialVersionUID = 1L;
        };
        user.setUserInfo( LuteceUser.NAME_GIVEN, "Jane" );
        user.setUserInfo( LuteceUser.NAME_FAMILY, "Doe" );
        SecurityService.getInstance( ).registerUser( request, user );

        XPage page = new MyLuteceTestApp( ).getPage( request, 0, null );

        assertEquals( "Application de test MyLutece", page.getTitle( ) );
        assertTrue( page.getContent( ).contains( "Utilisateur connecté : Jane Doe" ), page.getContent( ) );
        assertTrue( page.getContent( ).contains( "Aucun rôle" ), page.getContent( ) );
        assertFalse( page.getContent( ).contains( "#i18n" ), page.getContent( ) );
        assertFalse( page.getContent( ).contains( "Connected User" ), page.getContent( ) );
    }
}
