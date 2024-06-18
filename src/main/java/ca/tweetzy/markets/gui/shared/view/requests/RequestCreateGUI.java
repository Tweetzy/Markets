package ca.tweetzy.markets.gui.shared.view.requests;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.template.MaterialPickerGUI;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.MathUtil;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.input.TitleInput;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.Request;
import ca.tweetzy.markets.gui.MarketsBaseGUI;
import ca.tweetzy.markets.gui.shared.MarketsMainGUI;
import ca.tweetzy.markets.gui.shared.selector.CurrencyPickerGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public final class RequestCreateGUI extends MarketsBaseGUI {

	private final Gui parent;
	private final Request request;

	public RequestCreateGUI(Gui parent, @NonNull Player player, @NonNull final Request request) {
		super(parent, player, TranslationManager.string(player, Translations.GUI_CREATE_REQUEST_TITLE), 6);
		this.parent = parent;
		this.request = request;

		setAcceptsItems(true);
		setDefaultItem(QuickItem.bg(Settings.GUI_CREATE_REQUEST_BACKGROUND.getItemStack()));
		draw();
	}

	@Override
	protected void draw() {

		drawRequestItem();
		drawAmountButton();
		drawPriceButton();

		if (Settings.CURRENCY_ALLOW_PICK.getBoolean() || Settings.CURRENCY_USE_ITEM_ONLY.getBoolean())
			setButton(3, 6, QuickItem
					.of(Settings.GUI_CREATE_REQUEST_ITEMS_CURRENCY_ITEM.getItemStack())
					.name(TranslationManager.string(player, Translations.GUI_CREATE_REQUEST_ITEMS_CURRENCY_NAME))
					.lore(TranslationManager.list(player, Translations.GUI_CREATE_REQUEST_ITEMS_CURRENCY_LORE,
							"request_currency", this.request.getCurrencyDisplayName(),
							"left_click", TranslationManager.string(player, Translations.MOUSE_LEFT_CLICK)
					))
					.make(), click -> {

				click.manager.showGUI(click.player, new CurrencyPickerGUI(this, click.player, (currency, item) -> {
					click.gui.exit();

					this.request.setCurrency(currency.getStoreableName());

					if (item != null)
						this.request.setCurrencyItem(item);

					click.manager.showGUI(click.player, new RequestCreateGUI(RequestCreateGUI.this.parent, RequestCreateGUI.this.player, RequestCreateGUI.this.request));
				}));
			});

		setButton(getRows() - 1, 4, QuickItem
				.of(Settings.GUI_CREATE_REQUEST_ITEMS_CREATE_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_CREATE_REQUEST_ITEMS_CREATE_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_CREATE_REQUEST_ITEMS_CREATE_LORE,
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)
				))
				.make(), click -> {

			if (Markets.getPlayerManager().isAtRequestLimit(click.player)) {
				Common.tell(click.player, TranslationManager.string(click.player, Translations.AT_MAX_REQUEST_LIMIT));
				return;
			}

			if (this.request.getRequestItem() == null || this.request.getRequestItem().getType() == CompMaterial.AIR.parseMaterial()) {
				Common.tell(click.player, TranslationManager.string(click.player, Translations.PLACE_REQUEST_ITEM));
				return;
			}

			Markets.getRequestManager().create(
					click.player,
					this.request.getRequestItem(),
					this.request.getCurrency(),
					this.request.getCurrencyItem(),
					this.request.getPrice(),
					this.request.getRequestedAmount(),
					created -> {
						if (created)
							click.manager.showGUI(click.player, new RequestsGUI(new MarketsMainGUI(click.player), click.player, true));
					}
			);
		});

		applyBackExit();
	}

	private void drawAmountButton() {
		setButton(3, 2, QuickItem
				.of(Settings.GUI_CREATE_REQUEST_ITEMS_AMOUNT_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_CREATE_REQUEST_ITEMS_AMOUNT_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_CREATE_REQUEST_ITEMS_AMOUNT_LORE,
						"request_amount", this.request.getRequestedAmount(),
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)
				))
				.make(), click -> {

			if (this.request.getRequestItem().getType() == CompMaterial.AIR.parseMaterial()) {
				Common.tell(click.player, TranslationManager.string(click.player, Translations.PROVIDE_REQUESTED_ITEM));
				return;
			}

			new TitleInput(Markets.getInstance(), click.player, TranslationManager.string(this.player, Translations.PROMPT_REQUEST_AMOUNT_TITLE), TranslationManager.string(this.player, Translations.PROMPT_REQUEST_AMOUNT_SUBTITLE)) {

				@Override
				public void onExit(Player player) {
					click.manager.showGUI(click.player, RequestCreateGUI.this);
				}

				@Override
				public boolean onResult(String string) {
					string = ChatColor.stripColor(string);

					if (!MathUtil.isInt(string)) {
						Common.tell(click.player, TranslationManager.string(click.player, Translations.NOT_A_NUMBER, "value", string));
						return false;
					}

					final int requestAmount = Integer.parseInt(string);

					if (requestAmount <= 0) {
						Common.tell(click.player, TranslationManager.string(click.player, Translations.MUST_BE_HIGHER_THAN_ZERO, "value", string));
						return false;
					}

					if (requestAmount > RequestCreateGUI.this.request.getRequestItem().getMaxStackSize()) {
						Common.tell(click.player, TranslationManager.string(click.player, Translations.MAX_STACK_SIZE, "max_stack_size", RequestCreateGUI.this.request.getRequestItem().getMaxStackSize()));
						return false;
					}

					RequestCreateGUI.this.request.setRequestedAmount(requestAmount);
					click.manager.showGUI(click.player, new RequestCreateGUI(RequestCreateGUI.this.parent, RequestCreateGUI.this.player, RequestCreateGUI.this.request));
					return true;
				}
			};
		});
	}

	private void drawPriceButton() {
		setButton(3, 4, QuickItem
				.of(Settings.GUI_CREATE_REQUEST_ITEMS_PRICE_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_CREATE_REQUEST_ITEMS_PRICE_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_CREATE_REQUEST_ITEMS_PRICE_LORE,
						"request_price", String.format("%,.2f", this.request.getPrice()),
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)
				))
				.make(), click -> {

			if (this.request.getRequestItem().getType() == CompMaterial.AIR.parseMaterial()) {
				Common.tell(click.player, TranslationManager.string(click.player, Translations.PROVIDE_REQUESTED_ITEM));
				return;
			}

			new TitleInput(Markets.getInstance(), click.player, TranslationManager.string(this.player, Translations.PROMPT_REQUEST_PRICE_TITLE), TranslationManager.string(this.player, Translations.PROMPT_REQUEST_PRICE_SUBTITLE)) {

				@Override
				public void onExit(Player player) {
					click.manager.showGUI(click.player, RequestCreateGUI.this);
				}

				@Override
				public boolean onResult(String string) {
					string = ChatColor.stripColor(string);

					if (!MathUtil.isDouble(string)) {
						Common.tell(click.player, TranslationManager.string(click.player, Translations.NOT_A_NUMBER, "value", string));
						return false;
					}

					final double requestPrice = Double.parseDouble(string);

					if (requestPrice <= 0) {
						Common.tell(click.player, TranslationManager.string(click.player, Translations.MUST_BE_HIGHER_THAN_ZERO, "value", string));
						return false;
					}

					RequestCreateGUI.this.request.setPrice(requestPrice);
					click.manager.showGUI(click.player, new RequestCreateGUI(RequestCreateGUI.this.parent, RequestCreateGUI.this.player, RequestCreateGUI.this.request));
					return true;
				}
			};
		});
	}

	private void drawRequestItem() {
		final QuickItem quickItem = QuickItem
				.of(this.request.getRequestItem().getType() == CompMaterial.AIR.parseMaterial() ? CompMaterial.RED_STAINED_GLASS_PANE.parseItem() : this.request.getRequestItem())
				.lore(TranslationManager.list(this.player, Translations.GUI_CREATE_REQUEST_ITEMS_REQUESTED_ITEM_LORE,
						"left_click", TranslationManager.string(player, Translations.MOUSE_LEFT_CLICK),
						"right_click", TranslationManager.string(player, Translations.MOUSE_RIGHT_CLICK)
				));

		if (this.request.getRequestItem().getType() == CompMaterial.AIR.parseMaterial())
			quickItem.name(TranslationManager.string(player, Translations.GUI_CREATE_REQUEST_ITEMS_REQUESTED_ITEM_NAME));

		setButton(1, 4, quickItem.make(), click -> {

			if (click.clickType == ClickType.RIGHT) {
				final ItemStack cursor = click.cursor;
				if (cursor != null && cursor.getType() != CompMaterial.AIR.parseMaterial()) {
					final ItemStack newIcon = cursor.clone();
					newIcon.setAmount(1);

					this.request.setRequestedItem(newIcon);
					click.manager.showGUI(click.player, new RequestCreateGUI(RequestCreateGUI.this.parent, RequestCreateGUI.this.player, RequestCreateGUI.this.request));
				}
			}

			if (click.clickType == ClickType.LEFT) {
				click.manager.showGUI(click.player, new MaterialPickerGUI(this, null, "", (event, selected) -> {

					if (selected != null) {
						final ItemStack item = selected.parseItem();
						item.setAmount(1);

						this.request.setRequestedItem(item);
						click.manager.showGUI(click.player, new RequestCreateGUI(RequestCreateGUI.this.parent, RequestCreateGUI.this.player, RequestCreateGUI.this.request));
					}
				}));
			}
		});
	}
}
