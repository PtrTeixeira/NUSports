/*
 * Copyright (c) 2017 Peter Teixeira
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.ptrteixeira.nusports.model;

import java.io.IOException;
import org.jsoup.nodes.Document;

/**
 * A supplier for {@link Document} objects, which are parsed in the {@link WebScraper}. I am not
 * convinced that I have the right abstraction here. But it does permit unit testing, which is nice.
 *
 * @author Peter Teixeira
 */
interface DocumentSource {
  /**
   * Return the {@link Document} which can be accessed at the given URL.
   *
   * @param url URL of the web page to be accessed
   * @return {@code JSoup} interpretation of the accessed web-page
   * @throws IOException If the webpage cannot be accessed for whatever reason
   */
  Document get(String url) throws IOException;
}
