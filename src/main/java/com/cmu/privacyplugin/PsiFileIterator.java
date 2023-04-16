package com.cmu.privacyplugin;

import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class PsiFileIterator implements Iterator<PsiFile> {

    private final PsiManager psiManager;
    private final PsiDirectory rootDirectory;
    private Iterator<PsiFile> fileIterator;
    private ArrayList<Iterator<PsiFile>> fileIteratorsArray;
    private Iterator<Iterator<PsiFile>> fileIterators;
    private Iterator<PsiDirectory> rootDirectoryIterator;

    public PsiFileIterator(@NotNull Project project) {
        psiManager = PsiManager.getInstance(project);
        rootDirectory = psiManager.findDirectory(project.getBaseDir());
        rootDirectoryIterator = Arrays.stream(rootDirectory.getSubdirectories()).iterator();
        fileIteratorsArray = new ArrayList<>();


        buildFileIterators(rootDirectoryIterator);
        System.out.println("total fileIterators: " + fileIteratorsArray.size());
        fileIterators = fileIteratorsArray.iterator();
        fileIterator = fileIterators.next();
    }

    @Override
    public boolean hasNext() {
        return fileIterator.hasNext() || fileIterators.hasNext();
    }

    @Override
    public PsiFile next() {
        if (!fileIterator.hasNext()) {
            fileIterator = getNextFileIterator();
            while (!fileIterator.hasNext()) {
                // empty fileIterator
                fileIterator = getNextFileIterator();
            }
        }
        if (fileIterator == null)
        {
            System.out.println("file iterator is null");
            return null;
        }
        return fileIterator.next();
    }

    private Iterator<PsiFile> getNextFileIterator() {
        return fileIterators.next();
    }

    private void buildFileIterators(Iterator<PsiDirectory> directoryIterator) {
        if (!directoryIterator.hasNext()) {
            return;
        }
        while (directoryIterator.hasNext()) {
            PsiDirectory currentDirectory = directoryIterator.next();
            fileIteratorsArray.add(Arrays.stream(currentDirectory.getFiles()).iterator());
            Iterator<PsiDirectory> subdirectories = Arrays.stream(currentDirectory.getSubdirectories()).iterator();
            buildFileIterators(subdirectories);
        }
    }
}

