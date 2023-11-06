package io.github.furrrlo.dui.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;

public abstract class JavaBeanGenTask extends DefaultTask {

    @InputFiles
    public abstract ConfigurableFileCollection getClasspath();

    @Input
    public abstract MapProperty<String, String> getBeansToTargetPackages();

    @OutputDirectory
    public abstract DirectoryProperty getTargetDirectory();

    @Inject
    public abstract WorkerExecutor getWorkerExecutor();

    public JavaBeanGenTask() {
        getOutputs().upToDateWhen(s -> false);
    }

    @TaskAction
    protected void generate() {
        getWorkerExecutor().classLoaderIsolation(spec -> {
            spec.getClasspath().from(getClasspath());
        }).submit(JavaBeanWorkAction.class, params -> {
            params.getBeansToTargetPackages().set(getBeansToTargetPackages());
            params.getTargetDirectory().set(getTargetDirectory());
        });
    }
}
