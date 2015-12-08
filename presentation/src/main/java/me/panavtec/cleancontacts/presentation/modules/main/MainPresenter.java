package me.panavtec.cleancontacts.presentation.modules.main;

import java.util.List;
import me.panavtec.cleancontacts.domain.entities.Contact;
import me.panavtec.cleancontacts.domain.interactors.contacts.GetContactsError;
import me.panavtec.cleancontacts.domain.interactors.contacts.GetContactsInteractor;
import me.panavtec.cleancontacts.presentation.InteractorResult;
import me.panavtec.cleancontacts.presentation.Presenter;
import me.panavtec.cleancontacts.presentation.invoker.InteractorExecution;
import me.panavtec.cleancontacts.presentation.invoker.InteractorInvoker;
import me.panavtec.cleancontacts.presentation.model.PresentationContact;
import me.panavtec.cleancontacts.presentation.model.mapper.base.ListMapper;
import me.panavtec.threaddecoratedview.views.ThreadSpec;

public class MainPresenter extends Presenter<MainView> {

  private final InteractorInvoker interactorInvoker;
  private final GetContactsInteractor getContactsInteractor;
  private final ListMapper<Contact, PresentationContact> listMapper;

  public MainPresenter(InteractorInvoker interactorInvoker,
      GetContactsInteractor getContactsInteractor,
      final ListMapper<Contact, PresentationContact> listMapper, ThreadSpec mainThreadSpec) {
    super(mainThreadSpec);
    this.interactorInvoker = interactorInvoker;
    this.getContactsInteractor = getContactsInteractor;
    this.listMapper = listMapper;
  }

  @Override public void onViewAttached() {
    getView().initUi();
  }

  public void onResume() {
    refreshContactList();
  }

  public void onRefresh() {
    getView().refreshUi();
    refreshContactList();
  }

  private void refreshContactList() {
    new InteractorExecution<>(getContactsInteractor).result(new InteractorResult<List<Contact>>() {
      @Override public void onResult(List<Contact> result) {
        List<PresentationContact> presentationContacts = listMapper.modelToData(result);
        getView().refreshContactsList(presentationContacts);
      }
    }).error(GetContactsError.class, new InteractorResult<GetContactsError>() {
      @Override public void onResult(GetContactsError error) {
        getView().showGetContactsError();
      }
    }).execute(interactorInvoker);
  }
}
