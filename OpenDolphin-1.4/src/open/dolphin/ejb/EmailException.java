/*
 * Created on Feb 17, 2005
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL VERTICAL SLICE INC OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 * Copyright (c) 2005 Vertical Slice, Inc.
 * All rights reserved.
 */
package open.dolphin.ejb;

/**
 * EmailException
 *
 * @author Tom Marrs
 *
 */
public class EmailException extends RuntimeException {
    
    
    private static final long serialVersionUID = 1481106666395332557L;
    
    
    public EmailException() {
        super();
    }
    
    
    public EmailException(String message) {
        super(message);
    }
    
    
    public EmailException(Throwable cause) {
        super(cause);
    }
    
    
    public EmailException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
