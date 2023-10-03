package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class JDTabbedPane {

    public static DeclarativeComponent<JTabbedPane> fn(
            DeclarativeComponent.Body<JTabbedPane, Decorator<JTabbedPane>> body) {
        return fn(JTabbedPane.class, JTabbedPane::new, body);
    }

    public static DeclarativeComponent<JTabbedPane> fn(Supplier<JTabbedPane> factory,
                                                       DeclarativeComponent.Body<JTabbedPane, Decorator<JTabbedPane>> body) {
        return fn(JTabbedPane.class, factory, body);
    }

    public static <T extends JTabbedPane> DeclarativeComponent<T> fn(Class<T> type,
                                                                     Supplier<T> factory,
                                                                     DeclarativeComponent.Body<T, Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JTabbedPane> extends JDComponent.Decorator<T> {

        private static final String PREFIX = "__JTabbedPane__";

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
        }

        public void tabLayoutPolicy(Supplier<Integer> tabLayoutPolicy) {
            attribute(
                    PREFIX + "tabLayoutPolicy",
                    JTabbedPane::setTabLayoutPolicy,
                    tabLayoutPolicy);
        }

        public void tabPlacement(Supplier<Integer> tabPlacement) {
            attribute(
                    PREFIX + "tabPlacement",
                    JTabbedPane::setTabPlacement,
                    tabPlacement);
        }

        public void selectedTab(Supplier<Integer> index) {
            attribute(
                    PREFIX + "selectedTab",
                    JTabbedPane::setSelectedIndex,
                    index);
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        public void tabs(Consumer<TabCollector> collector) {
            // TODO: the tab collector does not use suppliers so it causes entire component updates
            final List<Tab<?>> tabs = new ArrayList<>();
            final List<TabComponent<? extends Component>> tabComponents0 = new ArrayList<>();
            collector.accept((key, title, icon, tabComponent, component, tooltipText) -> {
                tabs.add(new Tab<>(key, title, icon, component, tooltipText));
                tabComponents0.add(new TabComponent<>(tabComponent));
            });

            listFnAttribute(
                    PREFIX + "components",
                    (tabbedPane, idx, s, component) -> {
                        if (idx >= tabbedPane.getTabCount())
                            tabbedPane.addTab(s.title, s.icon, component, s.tooltipText);
                        else
                            tabbedPane.insertTab(s.title, s.icon, component, s.tooltipText, idx);
                    },
                    JTabbedPane::removeTabAt,
                    // No idea why this cast is even needed, IntelliJ says it's fine without while javac complains
                    (List<Tab<Component>>) (List) tabs);
            attribute(
                    PREFIX + "titles",
                    (tabbedPane, titles) -> {
                        for (int i = 0; i < titles.size(); i++) {
                            final String title = titles.get(i);
                            if (!Objects.equals(tabbedPane.getTitleAt(i), title))
                                tabbedPane.setTitleAt(i, title);
                        }
                    },
                    () -> tabs.stream().map(Tab::title).collect(Collectors.toList()));
            attribute(
                    PREFIX + "icons",
                    (tabbedPane, icons) -> {
                        for (int i = 0; i < icons.size(); i++) {
                            final Icon icon = icons.get(i);
                            if (!Objects.equals(tabbedPane.getIconAt(i), icon))
                                tabbedPane.setIconAt(i, icon);
                        }
                    },
                    () -> tabs.stream().map(Tab::icon).collect(Collectors.toList()));
            attribute(
                    PREFIX + "tooltipTexts",
                    (tabbedPane, toolTipTexts) -> {
                        for (int i = 0; i < toolTipTexts.size(); i++) {
                            final String toolTipText = toolTipTexts.get(i);
                            if (!Objects.equals(tabbedPane.getToolTipTextAt(i), toolTipText))
                                tabbedPane.setToolTipTextAt(i, toolTipText);
                        }
                    },
                    () -> tabs.stream().map(Tab::tooltipText).collect(Collectors.toList()));
            listFnAttribute(
                    PREFIX + "tabComponents",
                    (tabbedPane, s, tabComponents) -> {
                        for (int i = 0; i < tabComponents.size(); i++) {
                            final Component tabComponent = tabComponents.get(i);
                            if (!Objects.equals(tabbedPane.getTabComponentAt(i), tabComponent))
                                tabbedPane.setTabComponentAt(i, tabComponent);
                        }
                    },
                    // No idea why this cast is even needed, IntelliJ says it's fine without while javac complains
                    (List<TabComponent<Component>>) (List) tabComponents0);
        }

        public interface TabCollector {


            default void addTab(String title, DeclarativeComponentSupplier<? extends Component> component) {
                addTab(title, null, component);
            }

            default void addTab(String title, @Nullable Icon icon, DeclarativeComponentSupplier<? extends Component> component) {
                addTab(title, icon, component, null);
            }

            default void addTab(String title,
                                @Nullable Icon icon,
                                DeclarativeComponentSupplier<? extends Component> component,
                                @Nullable String tooltipText) {
                addTab(title, icon, null, component, tooltipText);
            }

            default void addTab(String title,
                                @Nullable Icon icon,
                                @Nullable DeclarativeComponentSupplier<? extends Component> tabComponent,
                                DeclarativeComponentSupplier<? extends Component> component,
                                @Nullable String tooltipText) {
                addTab(null, title, icon, tabComponent, component, tooltipText);
            }

            void addTab(@Nullable String key,
                        String title,
                        @Nullable Icon icon,
                        @Nullable DeclarativeComponentSupplier<? extends Component> tabComponent,
                        DeclarativeComponentSupplier<? extends Component> component,
                        @Nullable String tooltipText);
        }

        private static class Tab<T extends Component> implements DeclarativeComponentWithIdSupplier<T> {

            private final @Nullable String id;
            private final String title;
            private final @Nullable Icon icon;
            private final DeclarativeComponentSupplier<T> component;
            private final @Nullable String tooltipText;

            public Tab(@Nullable String id,
                       String title,
                       @Nullable Icon icon,
                       DeclarativeComponentSupplier<T> component,
                       @Nullable String tooltipText) {
                this.id = id;
                this.title = title;
                this.icon = icon;
                this.component = component;
                this.tooltipText = tooltipText;
            }

            @Override
            public DeclarativeComponent<T> apply(DeclarativeComponentFactory factory) {
                return component.apply(factory);
            }

            @Override
            public @Nullable String getId() {
                return id;
            }

            public String title() {
                return title;
            }

            public @Nullable Icon icon() {
                return icon;
            }

            public @Nullable String tooltipText() {
                return tooltipText;
            }

            @Override
            public String toString() {
                return "Tab{" +
                        "id='" + id + '\'' +
                        ", title='" + title + '\'' +
                        ", icon=" + icon +
                        ", component=" + component +
                        ", tooltipText='" + tooltipText + '\'' +
                        '}';
            }
        }

        private static class TabComponent<T extends Component> implements DeclarativeComponentWithIdSupplier<T> {

            private final @Nullable DeclarativeComponentSupplier<T> component;

            public TabComponent(@Nullable DeclarativeComponentSupplier<T> component) {
                this.component = component;
            }

            @Override
            public DeclarativeComponent<T> apply(DeclarativeComponentFactory factory) {
                return component != null ? component.apply(factory) : DNull.nullFn();
            }

            @Override
            public String getId() {
                return null;
            }
        }

        public void changeListener(Supplier<ChangeListener> l) {
            eventListener(PREFIX + "changeListener",
                    ChangeListener.class,
                    ChangeListenerWrapper::new,
                    JTabbedPane::addChangeListener,
                    l);
        }
    }
}
