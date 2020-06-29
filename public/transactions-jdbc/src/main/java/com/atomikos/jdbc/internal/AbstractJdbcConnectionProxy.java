/**
 * Copyright (C) 2000-2020 Atomikos <info@atomikos.com>
 *
 * LICENSE CONDITIONS
 *
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.jdbc.internal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.atomikos.logging.Logger;
import com.atomikos.logging.LoggerFactory;
import com.atomikos.util.DynamicProxySupport;
import com.atomikos.util.Proxied;

public abstract class AbstractJdbcConnectionProxy extends DynamicProxySupport<Connection> {

    private static final Logger LOGGER = LoggerFactory.createLogger(AbstractJdbcConnectionProxy.class);

    private List<Statement> statements = new ArrayList<Statement>();

    public AbstractJdbcConnectionProxy(Connection delegate) {
        super(delegate);
    }

    protected synchronized void addStatement(Statement s) {
        statements.add(s);
    }

    protected synchronized void removeStatement(Statement s) {
        statements.remove(s);
    }

    protected abstract void updateTransactionContext() throws SQLException;
    
    protected abstract boolean isEnlistedInGlobalTransaction();

    @Proxied
    public Statement createStatement() throws SQLException {
        updateTransactionContext();
        Statement s = delegate.createStatement();
        return createProxyStatement(s);
    }

	private <S extends Statement> S createProxyStatement(S s) {
		AtomikosJdbcStatementProxy<S> ajsp = new AtomikosJdbcStatementProxy<>(this, s);
        S proxy = ajsp.createDynamicProxy();
        addStatement(s);
        return proxy;
	}

    @Proxied
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        updateTransactionContext();
        PreparedStatement s = this.delegate.prepareStatement(sql);
        return createProxyStatement(s);
    }

    @Proxied
    public CallableStatement prepareCall(String sql) throws SQLException {
        updateTransactionContext();
        CallableStatement s = this.delegate.prepareCall(sql);
        return createProxyStatement(s);
    }
    

    @Proxied
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        updateTransactionContext();
        Statement s = this.delegate.createStatement(resultSetType, resultSetConcurrency);
        return createProxyStatement(s);
    }

    @Proxied
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
            throws SQLException {
        updateTransactionContext();
        PreparedStatement s = this.delegate.prepareStatement(sql, resultSetType, resultSetConcurrency);
        return createProxyStatement(s);
    }

    @Proxied
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        updateTransactionContext();
        CallableStatement s = this.delegate.prepareCall(sql, resultSetType, resultSetConcurrency);
        return createProxyStatement(s);
    }
    
    @Proxied
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
            throws SQLException {
        updateTransactionContext();
        Statement s = this.delegate.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        return createProxyStatement(s);
    }


    @Proxied
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        updateTransactionContext();
        PreparedStatement s = this.delegate.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        return createProxyStatement(s);
    }

    @Proxied
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {
        updateTransactionContext();
        CallableStatement s = this.delegate.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        return createProxyStatement(s);
    }

    @Proxied
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        updateTransactionContext();
        PreparedStatement s = this.delegate.prepareStatement(sql, autoGeneratedKeys);
        return createProxyStatement(s);
    }

    @Proxied
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        updateTransactionContext();
        PreparedStatement s = this.delegate.prepareStatement(sql, columnIndexes);
        return createProxyStatement(s);
    }

    @Proxied
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        updateTransactionContext();
        PreparedStatement s = this.delegate.prepareStatement(sql, columnNames);
        return createProxyStatement(s);
    }
    
    @Proxied
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if (isEnlistedInGlobalTransaction()) {
            if (autoCommit) {
                AtomikosSQLException.throwAtomikosSQLException(
                        "Cannot call 'setAutoCommit(true)' while a global transaction is running");
            }
        } else {
            delegate.setAutoCommit(autoCommit);
        }
    }

    @Proxied
    public boolean getAutoCommit() throws SQLException {
        if (isEnlistedInGlobalTransaction()) {
            return false;
        }
        return delegate.getAutoCommit();
    }

    @Proxied
    public void commit() throws SQLException {
        if (isEnlistedInGlobalTransaction()) {
            AtomikosSQLException
                    .throwAtomikosSQLException("Cannot call method 'commit' while a global transaction is running");
        }
        delegate.commit();
    }

    @Proxied
    public void rollback() throws SQLException {
        if (isEnlistedInGlobalTransaction()) {
            AtomikosSQLException.throwAtomikosSQLException(
                    "Cannot call method '" + "rollback" + "' while a global transaction is running");
        }
        delegate.rollback();

    }
    
    @Proxied
	public Savepoint setSavepoint() throws SQLException {
		if (isEnlistedInGlobalTransaction()) {
			AtomikosSQLException.throwAtomikosSQLException(
					"Cannot call method '" + "setSavepoint()" + "' while a global transaction is running");
		}
		return delegate.setSavepoint();
	}

	@Proxied
	public Savepoint setSavepoint(String name) throws SQLException {
		if (isEnlistedInGlobalTransaction()) {
			AtomikosSQLException.throwAtomikosSQLException(
					"Cannot call method '" + "setSavepoint(name)" + "' while a global transaction is running");
		}
		return delegate.setSavepoint(name);
	}

	@Proxied
	public void rollback(Savepoint savepoint) throws SQLException {
		if (isEnlistedInGlobalTransaction()) {
			AtomikosSQLException.throwAtomikosSQLException(
					"Cannot call method '" + "rollback(Savepoint)" + "' while a global transaction is running");
		}
		delegate.rollback(savepoint);

	}

	@Proxied
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		if (isEnlistedInGlobalTransaction()) {
			AtomikosSQLException.throwAtomikosSQLException(
					"Cannot call method '" + "releaseSavepoint(savepoint)" + "' while a global transaction is running");
		}
		delegate.releaseSavepoint(savepoint);

	}

	@Proxied
	public boolean isClosed() throws SQLException {
		return closed;
	}
	
	
	@Override
	protected void throwInvocationAfterClose(String methodName) throws AtomikosSQLException {
		String msg = "Connection was already closed - calling " + methodName + " is no longer allowed!";
		AtomikosSQLException.throwAtomikosSQLException(msg);

	}
	
	protected synchronized void forceCloseAllPendingStatements(boolean warn) {
        Iterator<Statement> it = statements.iterator();
        while (it.hasNext()) {
            Statement s = it.next();
            try {
                String msg = "Forcing close of pending statement: " + s;
                if (warn) {
                	LOGGER.logWarning(msg);
                } else {
                	LOGGER.logTrace(msg);
                }
                s.close();
            } catch (Exception e) {
                // ignore but log
                LOGGER.logWarning("Error closing pending statement: ", e);
            }
            // cf case 31275: also remove statement from list!
            it.remove();
        }
    }

}
