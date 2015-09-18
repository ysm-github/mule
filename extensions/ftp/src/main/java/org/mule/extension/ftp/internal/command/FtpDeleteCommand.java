/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.ftp.internal.command;

import static java.lang.String.format;
import org.mule.extension.ftp.internal.FtpConnector;
import org.mule.extension.ftp.internal.FtpFilePayload;
import org.mule.extension.ftp.internal.FtpFileSystem;
import org.mule.module.extension.file.api.FilePayload;
import org.mule.module.extension.file.api.command.DeleteCommand;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FtpDeleteCommand extends FtpCommand implements DeleteCommand
{

    private static Logger LOGGER = LoggerFactory.getLogger(FtpDeleteCommand.class);

    /**
     * {@inheritDoc}
     */
    public FtpDeleteCommand(FtpFileSystem fileSystem, FtpConnector config, FTPClient client)
    {
        super(fileSystem, config, client);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String filePath)
    {
        performDelete(filePath);
    }

    private void performDelete(String filePath)
    {
        FilePayload filePayload = getExistingFile(filePath);
        boolean isDirectory = filePayload.isDirectory();
        Path path = Paths.get(filePayload.getPath());

        if (isDirectory)
        {
            LOGGER.debug("Preparing to delete directory '{}'", path);
            deleteDirectory(path);
        }
        else
        {
            deleteFile(path);
        }
    }

    private void deleteFile(Path path)
    {
        fileSystem.verifyNotLocked(path);
        try
        {
            if (!client.deleteFile(path.toString()))
            {
                throw exception("Could not delete file " + path);
            }
        }
        catch (Exception e)
        {
            throw exception("Found Exception while deleting directory " + path, e);
        }
        logDelete(path);
    }

    private void deleteDirectory(Path path)
    {
        changeWorkingDirectory(path);
        FTPFile[] files;
        try
        {
            files = client.listFiles();
        }
        catch (IOException e)
        {
            throw exception(format("Could not list contents of directory '%s' while trying to delete it", path), e);
        }

        for (FTPFile file : files)
        {
            if (isVirtualDirectory(file.getName()))
            {
                continue;
            }

            FilePayload filePayload = new FtpFilePayload(path.resolve(file.getName()), file, config);

            final Path filePath = Paths.get(filePayload.getPath());
            if (filePayload.isDirectory())
            {
                deleteDirectory(filePath);
            }
            else
            {
                deleteFile(filePath);
            }
        }

        boolean removed;
        try
        {
            client.changeToParentDirectory();
            removed = client.removeDirectory(path.toString());
        }
        catch (IOException e)
        {
            throw exception("Found exception while trying to remove directory " + path, e);
        }

        if (!removed)
        {
            throw exception("Could not remove directory " + path);
        }

        logDelete(path);
    }

    private void logDelete(Path path)
    {
        LOGGER.debug("Successfully deleted '{}'", path);
    }
}
