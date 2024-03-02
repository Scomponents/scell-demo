package org.example.scelldemo.controls;

import com.intechcore.scomponents.scell.api.IScellApiResolver;
import com.intechcore.scomponents.scell.api.fx.IScellUiApi;
import com.intechcore.scomponents.scell.api.fx.IScellUiFxApiBuilder;
import com.intechcore.scomponents.scell.api.fx.control.IUiContentManager;
import com.intechcore.scomponents.scell.api.fx.control.IUiSelectionManager;
import com.intechcore.scomponents.scell.api.fx.model.ContextMenuOwner;
import com.intechcore.scomponents.scell.api.init.ScellApiEntryPoint;
import com.intechcore.scomponents.scell.api.spreadsheet.IScellCoreApiFactory;
import com.intechcore.scomponents.scell.api.spreadsheet.model.IWorkbook;
import com.intechcore.scomponents.scell.api.spreadsheet.model.data.IProductInfo;
import com.intechcore.scomponents.scell.api.spreadsheet.service.search.ISearchIterator;
import com.intechcore.scomponents.scell.api.spreadsheet.service.search.ISearchParams;
import com.intechcore.scomponents.scell.api.spreadsheet.service.search.ISearchResultItem;
import com.intechcore.scomponents.scell.api.spreadsheet.service.search.ISearchService;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import java.io.File;
import java.io.InputStream;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class ScellWrapper {
    private ISearchIterator[] iteratorClosure = new ISearchIterator[] { null };
    private IUiContentManager uiManager;
    private IUiSelectionManager selectionManager;
    private IProductInfo versionData;

    private final CompletableFuture<Node> scellControlFuture;

    public ScellWrapper(boolean disableContextMenus) {
        this.scellControlFuture = new CompletableFuture<>();
        ScellApiEntryPoint.getApiResolverAsync().thenApplyAsync(resolver -> {
            IScellUiApi<Node> uiApi = this.initServices(resolver, disableContextMenus);
            return uiApi.getControl();
        }, Platform::runLater).whenCompleteAsync((node, throwable) -> {
            if (throwable != null) {
                node = new Label("Failed to init: " + throwable.getCause().getMessage());
            }
            this.scellControlFuture.complete(node);
        }, Platform::runLater);
    }

    private IScellUiApi<Node> initServices(IScellApiResolver resolver, boolean disableContextMenus) {
        IScellCoreApiFactory coreFactory = IScellCoreApiFactory.resolve(resolver);

        CompletableFuture<IWorkbook> workbookFuture = CompletableFuture.supplyAsync(coreFactory::createNew);

        IScellUiApi<Node> uiApi = this.createUiBuilder(resolver, disableContextMenus).create(workbookFuture);
        this.uiManager = uiApi.getContentManager();
        this.selectionManager = uiApi.getSelectionManager();
        this.versionData = coreFactory.getProductInfo();

        return uiApi;
    }

    private IScellUiFxApiBuilder createUiBuilder(IScellApiResolver resolver, boolean disableContextMenus) {
        IScellUiFxApiBuilder result = IScellUiFxApiBuilder.resolve(resolver);
        if (disableContextMenus) {
            result.disableContextMenu(ContextMenuOwner.EDITING_CELL)
                  .disableContextMenu(ContextMenuOwner.GRID)
                  .disableContextMenu(ContextMenuOwner.TABS_PANEL);
        }
        result.readOnly(false);
        return result;
    }

    public CompletableFuture<Node> getScellControlFuture() {
        return this.scellControlFuture;
    }

    public CompletableFuture<Void> loadSpreadsheet(File file) {
        this.resetSearchIterator();
        if (file == null) {
            return CompletableFuture.completedFuture(null);
        }
        return this.uiManager.load(file);
    }

    public CompletableFuture<Void> loadSpreadsheet(InputStream content, String name) {
        this.resetSearchIterator();
        return this.uiManager.load(content, name);
    }

    public void createNew() {
        this.resetSearchIterator();
        this.uiManager.clear();
    }

    public void undo() {
        this.uiManager.undoLastAction();
    }

    public void redo() {
        this.uiManager.redoLastAction();
    }

    public CompletableFuture<Void> saveAs(File target) {
        if (target == null) {
            return CompletableFuture.completedFuture(null);
        }
        return this.uiManager.getWorkbook().thenAccept(workbook ->
                workbook.getWriter().fileName(target.getAbsolutePath()).save());
    }

    public String getApiVersionsInfo() {
        return String.join("\n",
            "SCell API Core Interfaces: \t\t\t"
                        + this.versionData.coreInterfaces(IProductInfo.ProductInfo.VERSION_WITH_BUILD_NUMBER),
                "SCell API Core Implementation: \t"
                        + this.versionData.coreImpl(IProductInfo.ProductInfo.VERSION_WITH_BUILD_NUMBER),
                "SCell API UI Interfaces: \t\t\t"
                        + this.versionData.uiInterfaces(IProductInfo.ProductInfo.VERSION_WITH_BUILD_NUMBER),
                "SCell API UI Implementation: \t\t"
                        + this.versionData.uiImpl(IProductInfo.ProductInfo.VERSION_WITH_BUILD_NUMBER));
    }

    public String getPlatformVersionsInfo() {
        return "JavaFX: " + this.versionData.getJavaFxVersion() + "\n"
                + "Java Runtime Version: " + this.versionData.getJavaVersionInfo();
    }

    public void addSearchActions(TextField patternInput, Button searchForwardButton, Window parentWindow) {
        patternInput.textProperty().addListener((event, oldValue, newValue) -> {
            this.resetSearchIterator();
            if (newValue.isEmpty()) {
                patternInput.setText("");
            }
        });

        searchForwardButton.setOnAction(event -> {
            if (patternInput.getText() == null || patternInput.getText().isEmpty()) {
                return;
            }

            CompletableFuture<Optional<ISearchResultItem>> findNextTask;
            if (iteratorClosure[0] == null) {
                CompletableFuture<ISearchIterator> searchIteratorCompletable = this.createSearchIterator(patternInput);
                findNextTask = searchIteratorCompletable.thenApply(searchIterator -> {
                    iteratorClosure[0] = searchIterator;
                    return searchIterator.getNextOrFromStart();
                });
            } else {
                findNextTask = CompletableFuture.supplyAsync(iteratorClosure[0]::getNextOrFromStart);
            }

            findNextTask.thenAcceptAsync(res -> {
                ISearchResultItem searchResult = res.orElse(null);
                if (searchResult == null) {
                    if (!patternInput.getText().isEmpty()) {
                        showNotFoundMsg(patternInput.getText(), parentWindow);
                    }
                    return;
                }

                this.selectionManager.setSelection(searchResult.getWorksheetId(), searchResult.getAddress());
            }, Platform::runLater);
        });
    }


    private void resetSearchIterator() {
        this.iteratorClosure[0] = null;
    }

    private static void showNotFoundMsg(String pattern, Window parent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(parent);
        alert.setHeaderText("Search");
        alert.setContentText("Not Found: " + pattern);
        alert.showAndWait();
    }

    private CompletableFuture<ISearchIterator> createSearchIterator(TextField patternInput) {
        return this.selectionManager.getActiveWorksheetId().thenCompose(worksheetId ->
                this.uiManager.getWorkbook().thenCombineAsync(this.selectionManager.getSelection(), (workbook, selection) -> {
                    ISearchService searchService = workbook.getSearchService();
                    ISearchParams searchContext = createSearchContext(searchService, patternInput);
                    return searchService.createSearchInWorkbookIterator(searchContext, worksheetId, selection);
                }));
    }

    private static ISearchParams createSearchContext(ISearchService searchService, TextField patternInput) {
        return searchService
                .createSearchParamsBuilder()
                .setPattern(patternInput.getText())
                .setCaseSensitive(false)
                .setWholeCell(false)
                .setSearchTarget(ISearchParams.SearchTarget.FORMATTED_VALUES)
                .build();
    }
}
