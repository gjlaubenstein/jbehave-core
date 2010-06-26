package org.jbehave.core.configuration;

import java.util.ArrayList;
import java.util.List;

import org.jbehave.core.annotations.UsingSteps;
import org.jbehave.core.annotations.Configure;
import org.jbehave.core.embedder.EmbedderControls;
import org.jbehave.core.failures.FailureStrategy;
import org.jbehave.core.failures.PendingStepStrategy;
import org.jbehave.core.io.StoryLoader;
import org.jbehave.core.io.StoryPathResolver;
import org.jbehave.core.parsers.StepPatternParser;
import org.jbehave.core.parsers.StoryParser;
import org.jbehave.core.reporters.StepdocReporter;
import org.jbehave.core.reporters.StoryReporter;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.reporters.ViewGenerator;
import org.jbehave.core.steps.CandidateSteps;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.jbehave.core.steps.ParameterConverters;
import org.jbehave.core.steps.StepCollector;
import org.jbehave.core.steps.StepFinder;
import org.jbehave.core.steps.StepMonitor;
import org.jbehave.core.steps.ParameterConverters.ParameterConverter;

import com.thoughtworks.paranamer.Paranamer;

public class AnnotationBuilder {

    private final AnnotationMonitor annotationMonitor;

    public AnnotationBuilder() {
        this(new PrintStreamAnnotationMonitor());
    }

    public AnnotationBuilder(AnnotationMonitor annotationMonitor) {
        this.annotationMonitor = annotationMonitor;
    }

    /**
     * Builds Configuration instance based on annotation
     * {@link Configure} found in the annotated object instance
     * 
     * @param annotatedInstance
     *            the Object instance that contains the annotations
     * @return A Configuration instance
     */
    public Configuration buildConfiguration(Object annotatedInstance) throws MissingAnnotationException {
        AnnotationFinder finder = new AnnotationFinder(annotatedInstance.getClass());

        Configuration configuration = new MostUsefulConfiguration();
        
        if (!finder.isAnnotationPresent(Configure.class)) {
            // not using annotation configuration, default to most useful configuration
            return configuration;
        }
        
        configuration.useKeywords(configurationElement(finder, "keywords", Keywords.class));
        configuration.useFailureStrategy(configurationElement(finder, "failureStrategy", FailureStrategy.class));
        configuration.usePendingStepStrategy(configurationElement(finder, "pendingStepStrategy",
                PendingStepStrategy.class));
        configuration.useParanamer(configurationElement(finder, "paranamer", Paranamer.class));
        configuration.useStepCollector(configurationElement(finder, "stepCollector", StepCollector.class));
        configuration.useStepdocReporter(configurationElement(finder, "stepdocReporter", StepdocReporter.class));
        configuration.useStepFinder(configurationElement(finder, "stepFinder", StepFinder.class));
        configuration.useStepMonitor(configurationElement(finder, "stepMonitor", StepMonitor.class));
        configuration.useStepPatternParser(configurationElement(finder, "stepPatternParser", StepPatternParser.class));
        configuration.useStoryLoader(configurationElement(finder, "storyLoader", StoryLoader.class));
        configuration.useStoryParser(configurationElement(finder, "storyParser", StoryParser.class));
        configuration.useStoryPathResolver(configurationElement(finder, "storyPathResolver", StoryPathResolver.class));
        configuration
                .useDefaultStoryReporter(configurationElement(finder, "defaultStoryReporter", StoryReporter.class));
        configuration.useStoryReporterBuilder(configurationElement(finder, "storyReporterBuilder",
                StoryReporterBuilder.class));
        configuration.useViewGenerator(configurationElement(finder, "viewGenerator", ViewGenerator.class));
        configuration.useEmbedderControls(configurationElement(finder, "embedderControls", EmbedderControls.class));
        configuration.useParameterConverters(parameterConverters(finder));
        return configuration;
    }

    /**
     * Builds CandidateSteps using annotation {@link UsingSteps} found in the
     * annotated object instance
     * 
     * @param annotatedInstance
     *            the Object instance that contains the annotations
     * @return A List of CandidateSteps instances
     */
    public List<CandidateSteps> buildCandidateSteps(Object annotatedInstance) {
        AnnotationFinder finder = new AnnotationFinder(annotatedInstance.getClass());
        List<Object> stepsInstances = new ArrayList<Object>();        
        Configuration configuration = buildConfiguration(annotatedInstance);
        InjectableStepsFactory factory = new InstanceStepsFactory(configuration);
        if (finder.isAnnotationPresent(UsingSteps.class)) {
            if ( finder.isAnnotationValuePresent(UsingSteps.class, "instances") ){
                List<Class<Object>> stepsClasses = finder.getAnnotatedClasses(UsingSteps.class, Object.class, "instances");
                for (Class<Object> stepsClass : stepsClasses) {
                    stepsInstances.add(instanceOf(Object.class, stepsClass));
                }             
                factory = new InstanceStepsFactory(configuration, stepsInstances);
            } else {
                annotationMonitor.annotationValueNotFound("instances", UsingSteps.class, annotatedInstance);
            }
        } else {
            annotationMonitor.annotationNotFound(UsingSteps.class, annotatedInstance);
        }

        return factory.createCandidateSteps();
    }

    private <T> T configurationElement(AnnotationFinder finder, String name, Class<T> type) {
        Class<T> implementation = elementImplementation(finder, name);
        return instanceOf(type, implementation);
    }

    @SuppressWarnings("unchecked")
    private <T> Class<T> elementImplementation(AnnotationFinder finder, String name) {
        return (Class<T>) finder.getAnnotatedValue(Configure.class, Class.class, name);
    }

    protected ParameterConverters parameterConverters(AnnotationFinder annotationFinder) {
        List<ParameterConverter> converters = new ArrayList<ParameterConverter>();
        for (Class<ParameterConverter> converterClass : annotationFinder.getAnnotatedClasses(Configure.class, ParameterConverter.class, "parameterConverters")) {
            converters.add(instanceOf(ParameterConverter.class, converterClass));
        }
        return new ParameterConverters().addConverters(converters);
    }

    protected <T> T instanceOf(Class<T> type, Class<T> ofClass) {
        try {
            return (T) ofClass.newInstance();
        } catch (Exception e) {
            annotationMonitor.elementCreationFailed(ofClass, e);
            throw new RuntimeException(e);
        }
    }

}