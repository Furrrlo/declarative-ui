package io.github.furrrlo.dui.swing;

import io.github.furrrlo.dui.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class JDTabbedPane {

    public static DeclarativeComponent<JTabbedPane> fn(IdentifiableConsumer<Decorator<JTabbedPane>> body) {
        return fn(JTabbedPane.class, JTabbedPane::new, body);
    }

    public static DeclarativeComponent<JTabbedPane> fn(Supplier<JTabbedPane> factory,
                                                       IdentifiableConsumer<Decorator<JTabbedPane>> body) {
        return fn(JTabbedPane.class, factory, body);
    }

    public static <T extends JTabbedPane> DeclarativeComponent<T> fn(Class<T> type,
                                                                     Supplier<T> factory,
                                                                     IdentifiableConsumer<Decorator<T>> body) {
        return DeclarativeComponentFactory.INSTANCE.of(() -> new Decorator<>(type, factory), body);
    }

    public static class Decorator<T extends JTabbedPane> extends JDComponent.Decorator<T> {

        private static final String PREFIX = "__JTabbedPane__";

        private final ReservedMemo<List<Tab<?, ?>>> reservedTabsMemo;

        protected Decorator(Class<T> type, Supplier<T> factory) {
            super(type, factory);
            reservedTabsMemo = reserveMemo(Collections::emptyList);
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

        public void tabs(IdentifiableConsumer<TabCollector> collector) {
            final Memo<List<Tab<?, ?>>> tabs = reservedTabsMemo.apply(IdentifiableSupplier.explicit(() -> {
                final List<Tab<?, ?>> tabs0 = new ArrayList<>();
                collector.accept((key, title, icon, tabComponent, component, tooltipText) ->
                        tabs0.add(new Tab<>(key, title, icon, new TabComponent<>(key, tabComponent), component, tooltipText)));
                return tabs0;
            }, collector));

            listFnAttribute(
                    PREFIX + "components",
                    (T tabbedPane, int idx, Tab<?, ?> s, Component component) -> {
                        if (idx >= tabbedPane.getTabCount())
                            tabbedPane.addTab(s.title, s.icon, component, s.tooltipText);
                        else
                            tabbedPane.insertTab(s.title, s.icon, component, s.tooltipText, idx);
                    },
                    JTabbedPane::removeTabAt,
                    tabs);
            attribute(
                    PREFIX + "titles",
                    (tabbedPane, titles) -> {
                        for (int i = 0; i < titles.size(); i++) {
                            final String title = titles.get(i);
                            if (!Objects.equals(tabbedPane.getTitleAt(i), title))
                                tabbedPane.setTitleAt(i, title);
                        }
                    },
                    () -> tabs.get().stream().map(Tab::title).collect(Collectors.toList()));
            attribute(
                    PREFIX + "icons",
                    (tabbedPane, icons) -> {
                        for (int i = 0; i < icons.size(); i++) {
                            final Icon icon = icons.get(i);
                            if (!Objects.equals(tabbedPane.getIconAt(i), icon))
                                tabbedPane.setIconAt(i, icon);
                        }
                    },
                    () -> tabs.get().stream().map(Tab::icon).collect(Collectors.toList()));
            attribute(
                    PREFIX + "tooltipTexts",
                    (tabbedPane, toolTipTexts) -> {
                        for (int i = 0; i < toolTipTexts.size(); i++) {
                            final String toolTipText = toolTipTexts.get(i);
                            if (!Objects.equals(tabbedPane.getToolTipTextAt(i), toolTipText))
                                tabbedPane.setToolTipTextAt(i, toolTipText);
                        }
                    },
                    () -> tabs.get().stream().map(Tab::tooltipText).collect(Collectors.toList()));
            listFnAttribute(
                    PREFIX + "tabComponents",
                    (T tabbedPane, int idx, TabComponent<?> s, Component component) ->
                            // the components attr should already have added everything, we just need to replace stuff
                            tabbedPane.setTabComponentAt(idx, component),
                    (tabbedPane, idx) -> {
                        // the components attr should already have removed everything
                    },
                    () -> tabs.get().stream().map(Tab::tabComponent).collect(Collectors.toList()));
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

        private static class Tab<T extends Component, TC extends Component> implements DeclarativeComponentWithIdSupplier<T> {

            private final @Nullable String id;
            private final String title;
            private final @Nullable Icon icon;
            private final TabComponent<TC> tabComponent;
            private final DeclarativeComponentSupplier<T> component;
            private final @Nullable String tooltipText;

            public Tab(@Nullable String id,
                       String title,
                       @Nullable Icon icon,
                       TabComponent<TC> tabComponent,
                       DeclarativeComponentSupplier<T> component,
                       @Nullable String tooltipText) {
                this.id = id;
                this.title = title;
                this.icon = icon;
                this.component = component;
                this.tabComponent = tabComponent;
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

            public TabComponent<TC> tabComponent() {
                return tabComponent;
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
                        ", tabComponent=" + tabComponent +
                        ", component=" + component +
                        ", tooltipText='" + tooltipText + '\'' +
                        '}';
            }
        }

        private static class TabComponent<T extends Component> implements DeclarativeComponentWithIdSupplier<T> {

            private final @Nullable String id;
            private final @Nullable DeclarativeComponentSupplier<T> component;

            public TabComponent(@Nullable String id, @Nullable DeclarativeComponentSupplier<T> component) {
                this.component = component;
                this.id = id;
            }

            @Override
            public DeclarativeComponent<T> apply(DeclarativeComponentFactory factory) {
                return component != null ? component.apply(factory) : DNull.nullFn();
            }

            @Override
            public String getId() {
                return id;
            }
        }

        public void changeListener(ChangeListener l) {
            eventListener(PREFIX + "changeListener",
                    ChangeListener.class,
                    ChangeListenerWrapper::new,
                    JTabbedPane::addChangeListener,
                    l);
        }
    }
}
