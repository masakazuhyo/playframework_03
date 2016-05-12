package controllers;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import play.*;
import play.data.Form;
import play.data.validation.ValidationError;
import play.mvc.*;
import views.html.*;
import models.Message;

public class Application extends Controller {
	public static Result index() {
		List<Message> datas = Message.find.all();
		return ok(index.render("ホーム画面", "データベースサンプル（Single）DBサンプル", datas));
	}

	public static Result add() {
		Form<Message> messageForm = Form.form(Message.class);
		return ok(add.render("新規メッセージ作成画面", "投稿フォーム", messageForm));
	}

	public static Result doAdd() {
		Form<Message> messageForm = Form.form(Message.class).bindFromRequest();
		if (!messageForm.hasErrors()) {
			messageForm.get().postdate = new Date(System.currentTimeMillis());
			messageForm.get().save();
			return redirect(routes.Application.index());
		}
		return ok(add.render("新規メッセージ作成画面", "投稿フォーム", messageForm));
	}

	public static Result delete() {
		Form<Message> messageForm = Form.form(Message.class);
		return ok(delete.render("投稿メッセージID削除画面（指定したIDを削除する）", "投稿フォーム", messageForm));
	}

	public static Result doDelete() {
		Form<Message> messageForm = Form.form(Message.class).bindFromRequest();
		if (!messageForm.hasErrors()) {
			if (messageForm.get().id != null) {
				Message message = Message.find.byId(messageForm.get().id);
				if (message != null) {
					message.delete();
					return redirect(routes.Application.index());
				}
			}
		}
		return ok(delete.render("投稿メッセージID削除画面（指定したIDを削除する）", "投稿フォーム", messageForm));
	}

	public static Result item() {
		Form<Message> messageForm = Form.form(Message.class);
		return ok(item.render("投稿メッセージID検索画面", "ID番号を入力。", messageForm));
	}

	public static Result edit() {
		Form<Message> messageForm = Form.form(Message.class).bindFromRequest();
		String msg = "";
		if (!messageForm.hasErrors()) {
			if (messageForm.get().id != null) {
				Message message = Message.find.byId(messageForm.get().id);
				if (message != null) {
					messageForm = messageForm.fill(message);
					msg = "投稿メッセージ編集画面";
					return ok(edit.render(msg, "ID=" + message.id + "の投稿を編集。", messageForm));
				}	else	{
					msg = "ERROR:IDの投稿が見つかりません。";
				}
			}
		}
		return ok(item.render("投稿メッセージ編集画面", msg, messageForm));
	}

	public static Result doEdit() {
		Long id = null;
		try {
			id = Long.parseLong(request().body().asFormUrlEncoded().get("id")[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (id != null) {
			Message message = Message.find.byId(id);
			Form<Message> messageForm = Form.form(Message.class).fill(message).bindFromRequest();
			if (!messageForm.hasErrors()) {
				messageForm.get().update();
				return redirect(routes.Application.index());
			} else {
				return redirect(routes.Application.index());
			}
		}
		return redirect(routes.Application.edit());
	}

	public static Result find() {
		Form<Message> messageForm = Form.form(Message.class).bindFromRequest();
		List<Message> messages = null;
		if(messageForm.hasErrors()) {
			return ok(find.render("投稿者検索画面", "投稿の検索", messages, messageForm));
		}	else	{
			if (messageForm.get().name != null) {
				messages = Message.find.where().eq("name", messageForm.get().name).findList();
			}
			return ok(find.render("投稿者検索画面", "投稿の検索", messages, messageForm));
		}
	}
}